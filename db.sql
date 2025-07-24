create table VENDORS
(
    ID           NUMBER       default "VENDOR"."ISEQ$$_77340".nextval generated as identity
		primary key,
    USERNAME     VARCHAR2(50)  not null
        unique,
    PASSWORD     VARCHAR2(255) not null,
    NAME         VARCHAR2(100) not null,
    PHONE        VARCHAR2(20),
    CREATED_DATE DATE         default SYSDATE,
    STATUS       VARCHAR2(10) default 'ACTIVE'
        check (status in ('ACTIVE', 'INACTIVE'))
)
/

create table MARKETS
(
    ID        NUMBER generated as identity
        primary key,
    NAME      VARCHAR2(50) not null,
    VENDOR_ID NUMBER
        references VENDORS
)
/

create table DEBTS
(
    ID           NUMBER       default "VENDOR"."ISEQ$$_77347".nextval generated as identity
		primary key,
    VENDOR_ID    NUMBER        not null
        references VENDORS,
    MARKET_ID    NUMBER
        references MARKETS,
    DEBTOR_PHONE VARCHAR2(20)  not null,
    DEBTOR_NAME  VARCHAR2(100) not null,
    AMOUNT       NUMBER        not null,
    BALANCE      NUMBER        not null,
    DESCRIPTION  VARCHAR2(255),
    DEBT_DATE    DATE          not null,
    DUE_DATE     DATE          not null,
    CREATED_DATE DATE          not null,
    STATUS       VARCHAR2(20) default 'PENDING'
        check (status in ('PENDING', 'PAID', 'OVERDUE'))
)
/

create index IDX_DEBTS_DEBTOR_NAME_UPPER
    on DEBTS (UPPER("DEBTOR_NAME"))
/

create index IDX_DEBTS_DEBTOR_PHONE_UPPER
    on DEBTS (UPPER("DEBTOR_PHONE"))
/

create table DEBT_TRANSACTION
(
    ID               NUMBER generated as identity
        primary key,
    DEBT_ID          NUMBER not null
        references DEBTS,
    TRANSACTION_TYPE VARCHAR2(20)
        check (transaction_type in ('CREDIT', 'DEBIT')),
    AMOUNT           NUMBER not null,
    DESCRIPTION      VARCHAR2(255),
    CREATED_DATE     DATE   not null,
    STATUS           VARCHAR2(20)
        check (status in ('SUCCESS', 'FAIL'))
)
/

create or replace trigger TRG_UPDATE_DEBT_AFTER_TRANSACTION
    after insert or update or delete
    on DEBT_TRANSACTION
    for each row
DECLARE
    v_debt_id         NUMBER;
    v_current_balance NUMBER;
    v_original_amount NUMBER;
    v_current_status  VARCHAR2(20);
    v_balance_change  NUMBER := 0;
BEGIN
    IF INSERTING OR UPDATING THEN
        v_debt_id := :NEW.DEBT_ID;
    ELSIF DELETING THEN
        v_debt_id := :OLD.DEBT_ID;
    END IF;

    SELECT AMOUNT, BALANCE, STATUS
    INTO v_original_amount, v_current_balance, v_current_status
    FROM DEBTS
    WHERE ID = v_debt_id;

    IF INSERTING THEN
        IF :NEW.STATUS = 'SUCCESS' THEN
            IF :NEW.TRANSACTION_TYPE = 'CREDIT' THEN
                v_balance_change := -:NEW.AMOUNT;
            ELSIF :NEW.TRANSACTION_TYPE = 'DEBIT' THEN
                v_balance_change := :NEW.AMOUNT;
            END IF;
        END IF;
    ELSIF UPDATING THEN
        IF :OLD.STATUS = 'SUCCESS' THEN
            IF :OLD.TRANSACTION_TYPE = 'CREDIT' THEN
                v_balance_change := :OLD.AMOUNT;
            ELSIF :OLD.TRANSACTION_TYPE = 'DEBIT' THEN
                v_balance_change := -:OLD.AMOUNT;
            END IF;
        END IF;

        IF :NEW.STATUS = 'SUCCESS' THEN
            IF :NEW.TRANSACTION_TYPE = 'CREDIT' THEN
                v_balance_change := v_balance_change - :NEW.AMOUNT;
            ELSIF :NEW.TRANSACTION_TYPE = 'DEBIT' THEN
                v_balance_change := v_balance_change + :NEW.AMOUNT;
            END IF;
        END IF;
    ELSIF DELETING THEN
        IF :OLD.STATUS = 'SUCCESS' THEN
            IF :OLD.TRANSACTION_TYPE = 'CREDIT' THEN
                v_balance_change := :OLD.AMOUNT;
            ELSIF :OLD.TRANSACTION_TYPE = 'DEBIT' THEN
                v_balance_change := -:OLD.AMOUNT;
            END IF;
        END IF;
    END IF;

    v_current_balance := v_current_balance + v_balance_change;

    IF v_current_balance < 0 THEN
        v_current_balance := 0;
    END IF;

    IF v_current_balance = 0 THEN
        v_current_status := 'PAID';
    ELSIF v_current_status = 'PAID' AND v_current_balance > 0 THEN
        SELECT CASE
                   WHEN DUE_DATE < SYSDATE THEN 'OVERDUE'
                   ELSE 'PENDING'
                   END
        INTO v_current_status
        FROM DEBTS
        WHERE ID = v_debt_id;
    END IF;

    UPDATE DEBTS
    SET BALANCE = v_current_balance,
        STATUS  = v_current_status
    WHERE ID = v_debt_id;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error in debt update trigger: ' || SQLERRM);
        RAISE;
END;
/

create or replace function get_vendor_total_debt(p_vendor_id number)
    return number
    is
    v_total number := 0;
begin
    select nvl(sum(amount), 0)
    into v_total
    from debts d
    where d.vendor_id = p_vendor_id;
    return v_total;
end;
/

create or replace function get_debt_stats_by_date(
    p_vendor_id number,
    p_start_date date,
    p_end_date date)
    return sys_refcursor
    is
    v_cursor sys_refcursor;
begin
    open v_cursor for
        select count(*)                                                           as total_debts,
               nvl(sum(d.amount), 0)                                              as total_amount,
               nvl(sum(d.balance), 0)                                             as total_balance,
               avg(d.balance)                                                     as average_balance,
               count(case when d.status = 'PENDING' then 1 end)                   as total_pending,
               count(case when d.status = 'PAID' then 1 end)                      as total_paid,
               count(case when d.due_date < sysdate and d.balance > 0 then 1 end) as total_overdue,
               case
                   when nvl(sum(d.amount), 0) = 0 then 0
                   when nvl(sum(d.balance), 0) = 0 then 100
                   else round((1 - nvl(sum(d.balance), 0) / nvl(sum(d.amount), 0)) * 100, 0)
                   end                                                            as payment_percentage
        from debts d
        where d.vendor_id = p_vendor_id
          and d.debt_date between p_start_date and p_end_date;
    return v_cursor;
end;
/

create or replace PROCEDURE search_debts(
    p_debtor_phone IN VARCHAR2 DEFAULT NULL,
    p_debtor_name IN VARCHAR2 DEFAULT NULL,
    p_vendor_id IN NUMBER DEFAULT NULL,
    p_status IN VARCHAR2 DEFAULT NULL,
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
        SELECT
            d.id,
            d.vendor_id,
            d.debtor_name,
            d.debtor_phone,
            d.amount,
            d.balance,
            d.description,
            d.debt_date,
            d.due_date,
            d.created_date,
            d.status
        FROM DEBTS d
        WHERE 1 = 1
          AND (p_vendor_id IS NULL OR d.vendor_id = p_vendor_id)
          AND (p_debtor_phone IS NULL OR
               UPPER(d.debtor_phone) LIKE '%' || UPPER(p_debtor_phone) || '%' OR
               UPPER(d.debtor_name) LIKE '%' || UPPER(p_debtor_name) || '%')
          AND (p_status IS NULL OR d.status = p_status)
        ORDER BY d.created_date DESC;
END search_debts;
/

create or replace procedure get_debt_transactions(
    p_debt_id in number,
    p_cursor out sys_refcursor
)
    is
begin
    open p_cursor for
        select dt.id           as transaction_id,
               dt.transaction_type,
               dt.amount       as transaction_amount,
               dt.description  as transaction_description,
               dt.created_date as transaction_date,
               dt.status       as transaction_status,
               d.debtor_name,
               d.debtor_phone,
               d.amount        as original_debt_amount,
               d.balance       as current_balance
        from debt_transaction dt
                 inner join debts d on dt.debt_id = d.id
        where dt.debt_id = p_debt_id
        order by dt.created_date desc;
end;
/

create or replace function get_overdue_debts_total(
    p_vendor_id in number default null
) return number
    is
    v_total number := 0;
begin
    select nvl(sum(d.balance), 0)
    into v_total
    from debts d
    where d.due_date < sysdate
      and d.balance > 0
      and (p_vendor_id is null or d.vendor_id = p_vendor_id);

    return v_total;
end;
/

create or replace function get_vendor_total_balance(p_vendor_id number)
    return number
    is
    v_total number := 0;
begin
    select nvl(sum(balance), 0)
    into v_total
    from debts d
    where d.vendor_id = p_vendor_id;
    return v_total;
end;
/

create or replace PROCEDURE update_overdue_debts AS
BEGIN
    UPDATE DEBTS
    SET STATUS = 'OVERDUE'
    WHERE BALANCE > 0
      AND DUE_DATE < SYSDATE
      AND STATUS != 'OVERDUE'
      AND STATUS != 'PAID';
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_overdue_debts;
/

create or replace function get_all_debt_stats(p_vendor_id number)
    return sys_refcursor is
    v_cursor sys_refcursor;
begin
    open v_cursor for
        select count(*)                                                                                    as total_debts,
               nvl(sum(d.amount), 0)                                                                       as total_amount,
               nvl(sum(d.balance), 0)                                                                      as total_balance,
               avg(d.balance)                                                                              as average_balance,
               count(case when d.status = 'PENDING' then 1 end)                                            as total_pending,
               count(case when d.status = 'PAID' then 1 end)                                               as total_paid,
               count(case
                         when d.status = 'OVERDUE' or (d.due_date < sysdate and d.balance > 0)
                             then 1 end)                                                                   as total_overdue,
               case
                   when nvl(sum(d.amount), 0) = 0 then 0
                   when nvl(sum(d.balance), 0) = 0 then 100
                   else round((1 - nvl(sum(d.balance), 0) / nvl(sum(d.amount), 0)) * 100, 0)
                   end                                                                                     as payment_percentage
        from debts d
        where d.vendor_id = p_vendor_id;
    return v_cursor;
end;
/

create or replace function get_vendor_all_debts(p_vendor_id number)
    return sys_refcursor is
    v_cursor sys_refcursor;
begin
    open v_cursor for
        select d.id,
               debtor_phone,
               debtor_name,
               amount,
               balance,
               description,
               debt_date,
               due_date,
               status
        from debts d
        where vendor_id = p_vendor_id
        order by d.id;
    return v_cursor;
end;
/