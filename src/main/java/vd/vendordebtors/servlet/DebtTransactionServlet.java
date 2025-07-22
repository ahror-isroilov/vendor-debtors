package vd.vendordebtors.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vd.vendordebtors.dao.DebtTransactionDao;
import vd.vendordebtors.model.Vendor;
import vd.vendordebtors.model.debt.DebtTransaction;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "debtTransactionServlet", value = "/debt-transactions")
public class DebtTransactionServlet extends HttpServlet {

    private final DebtTransactionDao debtTransactionDao = new DebtTransactionDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Vendor vendor = (Vendor) req.getSession().getAttribute("vendor");
        if (vendor == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String debtIdParam = req.getParameter("debtId");
        if (debtIdParam == null || debtIdParam.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int debtId = Integer.parseInt(debtIdParam);
            List<DebtTransaction> transactions = debtTransactionDao.getDebtTransactions(debtId);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(transactions));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Vendor vendor = (Vendor) req.getSession().getAttribute("vendor");
        if (vendor == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            createTransaction(req, resp);
        } else if ("delete".equals(action)) {
            deleteTransaction(req, resp);
        }
    }

    private void createTransaction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String debtIdParam = req.getParameter("debtId");
            String transactionType = req.getParameter("transactionType");
            String amountParam = req.getParameter("amount");
            String description = req.getParameter("description");

            if (debtIdParam == null || transactionType == null || amountParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int debtId = Integer.parseInt(debtIdParam);
            java.math.BigDecimal amount = new java.math.BigDecimal(amountParam);

            if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            DebtTransaction transaction = new DebtTransaction();
            transaction.setDebtId(debtId);
            transaction.setTransactionType(transactionType);
            transaction.setAmount(amount);
            transaction.setDescription(description != null ? description.trim() : null);
            transaction.setCreatedDate(new java.util.Date());

            boolean success = debtTransactionDao.createTransaction(transaction);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void deleteTransaction(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String transactionIdParam = req.getParameter("transactionId");
            
            if (transactionIdParam == null || transactionIdParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            int transactionId = Integer.parseInt(transactionIdParam);
            boolean success = debtTransactionDao.deleteTransaction(transactionId);
            
            if (success) {
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}