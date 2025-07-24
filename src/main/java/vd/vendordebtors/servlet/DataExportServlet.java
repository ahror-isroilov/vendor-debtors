package vd.vendordebtors.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vd.vendordebtors.dao.StatsDao;
import vd.vendordebtors.model.DetailedStats;
import vd.vendordebtors.model.ExportData;
import vd.vendordebtors.model.Vendor;
import vd.vendordebtors.model.debt.Debt;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "exportServlet", value = "/export")
public class DataExportServlet extends HttpServlet {
    private final StatsDao statsDao = new StatsDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Vendor vendor = (Vendor) session.getAttribute("vendor");

        try {
            ExportData data = statsDao.getExportData(vendor.getId());
            Workbook workbook = createExcelFile(data, vendor.getName());
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition",
                    "attachment; filename=vendor_debts-%s.xlsx".formatted(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())));
            workbook.write(resp.getOutputStream());
            workbook.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Workbook createExcelFile(ExportData data, String vendorName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Debt Report");

        CellStyle titleStyle = buildTitleStyle(workbook);
        CellStyle headerStyle = buildHeaderStyle(workbook);
        CellStyle labelStyle = buildLabelStyle(workbook);
        CellStyle valueStyle = buildValueStyle(workbook);
        CellStyle currencyStyle = buildCurrencyStyle(workbook);
        CellStyle percentageStyle = buildPercentageStyle(workbook);
        CellStyle tableHeaderStyle = buildTableHeaderStyle(workbook);
        CellStyle dataStyle = buildDataStyle(workbook);
        CellStyle alternateDataStyle = buildAlternateDataStyle(workbook);

        int currentRow = 0;

        Row titleRow = sheet.createRow(currentRow++);
        titleRow.setHeightInPoints(24);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("Debt Management Report");
        titleCell.setCellStyle(titleStyle);

        Row vendorRow = sheet.createRow(currentRow++);
        vendorRow.setHeightInPoints(16);
        Cell vendorCell = vendorRow.createCell(1);
        vendorCell.setCellValue(vendorName + " • " + new SimpleDateFormat("MMM dd, yyyy").format(new java.util.Date()));
        vendorCell.setCellStyle(headerStyle);

        currentRow++;

        DetailedStats stats = data.stats();

        String[][] statsData = {
                {"Total Records", String.valueOf(stats.getTotalDebts())},
                {"Total Amount", formatCurrency(stats.getTotalAmount())},
                {"Outstanding", formatCurrency(stats.getTotalBalance())},
                {"Average Balance", formatCurrency(stats.getAverageBalance())},
                {"Paid", String.valueOf(stats.getTotalPaid())},
                {"Pending", String.valueOf(stats.getTotalPending())},
                {"Overdue", String.valueOf(stats.getTotalOverdue())},
                {"Payment Rate", stats.getPaymentPercentage() + "%"}
        };

        for (String[] statRow : statsData) {
            Row row = sheet.createRow(currentRow++);
            row.setHeightInPoints(16);

            Cell labelCell = row.createCell(1);
            labelCell.setCellValue(statRow[0]);
            labelCell.setCellStyle(labelStyle);

            Cell valueCell = row.createCell(2);
            String value = statRow[1];

            if (value.contains("$")) {
                String numericValue = value.replace("$", "").replace(",", "");
                try {
                    valueCell.setCellValue(Double.parseDouble(numericValue));
                    valueCell.setCellStyle(currencyStyle);
                } catch (NumberFormatException e) {
                    valueCell.setCellValue(value);
                    valueCell.setCellStyle(valueStyle);
                }
            } else if (value.contains("%")) {
                String percentValue = value.replace("%", "");
                try {
                    valueCell.setCellValue(Double.parseDouble(percentValue) / 100.0);
                    valueCell.setCellStyle(percentageStyle);
                } catch (NumberFormatException e) {
                    valueCell.setCellValue(value);
                    valueCell.setCellStyle(valueStyle);
                }
            } else {
                try {
                    valueCell.setCellValue(Integer.parseInt(value));
                    valueCell.setCellStyle(valueStyle);
                } catch (NumberFormatException e) {
                    valueCell.setCellValue(value);
                    valueCell.setCellStyle(valueStyle);
                }
            }
        }

        currentRow++;

        Row tableHeaderRow = sheet.createRow(currentRow++);
        tableHeaderRow.setHeightInPoints(18);
        String[] tableHeaders = {
                "ID", "Debtor", "Phone", "Amount", "Balance", "Paid", "Debt Date", "Due Date", "Status", "Description"
        };

        for (int i = 0; i < tableHeaders.length; i++) {
            Cell cell = tableHeaderRow.createCell(i);
            cell.setCellValue(tableHeaders[i]);
            cell.setCellStyle(tableHeaderStyle);
        }

        List<Debt> debts = data.debts();

        for (int debtIndex = 0; debtIndex < debts.size(); debtIndex++) {
            Debt debt = debts.get(debtIndex);
            Row row = sheet.createRow(currentRow++);
            row.setHeightInPoints(16);

            CellStyle rowStyle = (debtIndex % 2 == 0) ? dataStyle : alternateDataStyle;

            Cell idCell = row.createCell(0);
            idCell.setCellValue(debt.getId());
            idCell.setCellStyle(rowStyle);

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(debt.getDebtorName());
            nameCell.setCellStyle(rowStyle);

            Cell phoneCell = row.createCell(2);
            phoneCell.setCellValue(debt.getDebtorPhone() != null ? debt.getDebtorPhone() : "");
            phoneCell.setCellStyle(rowStyle);

            Cell amountCell = row.createCell(3);
            amountCell.setCellValue(debt.getAmount().doubleValue());
            amountCell.setCellStyle(buildCurrencyDataStyle(workbook, debtIndex % 2 == 0));

            Cell balanceCell = row.createCell(4);
            balanceCell.setCellValue(debt.getBalance().doubleValue());
            balanceCell.setCellStyle(buildCurrencyDataStyle(workbook, debtIndex % 2 == 0));

            Cell paidCell = row.createCell(5);
            paidCell.setCellValue(debt.getPaidAmount().doubleValue());
            paidCell.setCellStyle(buildCurrencyDataStyle(workbook, debtIndex % 2 == 0));

            Cell debtDateCell = row.createCell(6);
            if (debt.getDebtDate() != null) {
                debtDateCell.setCellValue(debt.getDebtDate());
                debtDateCell.setCellStyle(buildDateDataStyle(workbook, debtIndex % 2 == 0));
            } else {
                debtDateCell.setCellStyle(rowStyle);
            }

            Cell dueDateCell = row.createCell(7);
            if (debt.getDueDate() != null) {
                dueDateCell.setCellValue(debt.getDueDate());
                dueDateCell.setCellStyle(buildDateDataStyle(workbook, debtIndex % 2 == 0));
            } else {
                dueDateCell.setCellStyle(rowStyle);
            }

            Cell statusCell = row.createCell(8);
            statusCell.setCellValue(debt.getStatus());
            statusCell.setCellStyle(buildStatusStyle(workbook, debt.getStatus(), debtIndex % 2 == 0));

            Cell descCell = row.createCell(9);
            descCell.setCellValue(debt.getDescription() != null ? debt.getDescription() : "");
            descCell.setCellStyle(rowStyle);
        }

        int[] columnWidths = {800, 2500, 3000, 2800, 2800, 2800, 2400, 2400, 2200, 4000};
        for (int i = 0; i < columnWidths.length && i < 10; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));

        return workbook;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return String.format("$%,.2f", amount.doubleValue());
    }

    private CellStyle buildTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildLabelStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildValueStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildCurrencyStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildPercentageStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.0%"));
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildTableHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 75, (byte) 85, (byte) 99}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorderStyle(style);
        return style;
    }

    private CellStyle buildDataStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorderStyle(style);
        return style;
    }

    private CellStyle buildAlternateDataStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 249, (byte) 250, (byte) 251}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorderStyle(style);
        return style;
    }

    private CellStyle buildCurrencyDataStyle(XSSFWorkbook workbook, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (!isEvenRow) {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 249, (byte) 250, (byte) 251}, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        addBorderStyle(style);
        return style;
    }

    private CellStyle buildDateDataStyle(XSSFWorkbook workbook, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("mm/dd/yyyy"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (!isEvenRow) {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 249, (byte) 250, (byte) 251}, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        addBorderStyle(style);
        return style;
    }

    private CellStyle buildStatusStyle(XSSFWorkbook workbook, String status, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setBold(true);

        switch (status.toUpperCase()) {
            case "PAID":
                font.setColor(new XSSFColor(new byte[]{(byte) 22, (byte) 101, (byte) 52}, null));
                break;
            case "PENDING":
                font.setColor(new XSSFColor(new byte[]{(byte) 161, (byte) 98, (byte) 7}, null));
                break;
            case "OVERDUE":
                font.setColor(new XSSFColor(new byte[]{(byte) 185, (byte) 28, (byte) 28}, null));
                break;
            default:
                font.setColor(IndexedColors.BLACK.getIndex());
        }

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        if (!isEvenRow) {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 249, (byte) 250, (byte) 251}, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        addBorderStyle(style);
        return style;
    }

    private void addBorderStyle(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFColor borderColor = new XSSFColor(new byte[]{(byte) 229, (byte) 231, (byte) 235}, null);
        style.setTopBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
    }
}