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
        CellStyle subtitleStyle = buildSubtitleStyle(workbook);
        CellStyle sectionHeaderStyle = buildSectionHeaderStyle(workbook);
        CellStyle statsKeyStyle = buildStatsLabelStyle(workbook);
        CellStyle statsValueStyle = buildStatsValueStyle(workbook);
        CellStyle positiveCurrencyStyle = buildCurrencyPositiveStyle(workbook);
        CellStyle negativeCurrencyStyle = buildCurrencyNegativeStyle(workbook);
        CellStyle percentageStyle = buildPercentageStyle(workbook);
        CellStyle headerStyle = buildTableHeaderStyle(workbook);
        CellStyle dataStyle = buildTableDataStyle(workbook);
        CellStyle alternateDataStyle = buildTableDataAlternateStyle(workbook);

        int currentRow = 0;

        Row titleRow = sheet.createRow(currentRow++);
        titleRow.setHeightInPoints(35);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DEBT MANAGEMENT REPORT");
        titleCell.setCellStyle(titleStyle);

        Row vendorRow = sheet.createRow(currentRow++);
        vendorRow.setHeightInPoints(25);
        Cell vendorCell = vendorRow.createCell(0);
        vendorCell.setCellValue(vendorName);
        vendorCell.setCellStyle(subtitleStyle);

        Row dateRow = sheet.createRow(currentRow++);
        dateRow.setHeightInPoints(20);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Generated on: " + new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm").format(new java.util.Date()));
        dateCell.setCellStyle(buildDateHeaderStyle(workbook));

        currentRow += 2;

        Row statsHeaderRow = sheet.createRow(currentRow++);
        statsHeaderRow.setHeightInPoints(30);
        Cell statsHeaderCell = statsHeaderRow.createCell(0);
        statsHeaderCell.setCellValue("FINANCIAL OVERVIEW");
        statsHeaderCell.setCellStyle(sectionHeaderStyle);

        currentRow++;

        DetailedStats stats = data.stats();

        String[][] statsData = {
                {"Total Debt Records", String.valueOf(stats.getTotalDebts()), "count"},
                {"Total Debt Amount", formatCurrency(stats.getTotalAmount()), "currency"},
                {"Outstanding Balance", formatCurrency(stats.getTotalBalance()), "currency"},
                {"Average Balance", formatCurrency(stats.getAverageBalance()), "currency"},
                {"", "", ""},
                {"Total Paid", String.valueOf(stats.getTotalPaid()), "count"},
                {"Total Pending", String.valueOf(stats.getTotalPending()), "count"},
                {"Total Overdue", String.valueOf(stats.getTotalOverdue()), "count"},
                {"Payment Rate", stats.getPaymentPercentage() + "%", "percentage"}
        };

        for (String[] statRow : statsData) {
            if (statRow[0].isEmpty()) {
                currentRow++;
                continue;
            }

            Row row = sheet.createRow(currentRow++);
            row.setHeightInPoints(22);

            Cell labelCell = row.createCell(1);
            labelCell.setCellValue(statRow[0]);
            labelCell.setCellStyle(statsKeyStyle);

            Cell valueCell = row.createCell(2);
            String value = statRow[1];
            String type = statRow[2];

            switch (type) {
                case "currency":
                    String numericValue = value.replace("$", "").replace(",", "");
                    try {
                        double amount = Double.parseDouble(numericValue);
                        valueCell.setCellValue(amount);
                        valueCell.setCellStyle(amount < 0 ? negativeCurrencyStyle : positiveCurrencyStyle);
                    } catch (NumberFormatException e) {
                        valueCell.setCellValue(value);
                        valueCell.setCellStyle(statsValueStyle);
                    }
                    break;
                case "percentage":
                    String percentValue = value.replace("%", "");
                    try {
                        double percentage = Double.parseDouble(percentValue) / 100.0;
                        valueCell.setCellValue(percentage);
                        valueCell.setCellStyle(percentageStyle);
                    } catch (NumberFormatException e) {
                        valueCell.setCellValue(value);
                        valueCell.setCellStyle(statsValueStyle);
                    }
                    break;
                case "count":
                    try {
                        int intValue = Integer.parseInt(value);
                        valueCell.setCellValue(intValue);
                        valueCell.setCellStyle(statsValueStyle);
                    } catch (NumberFormatException e) {
                        valueCell.setCellValue(value);
                        valueCell.setCellStyle(statsValueStyle);
                    }
                    break;
                default:
                    valueCell.setCellValue(value);
                    valueCell.setCellStyle(statsValueStyle);
            }
        }

        currentRow += 3;

        Row debtHeaderRow = sheet.createRow(currentRow++);
        debtHeaderRow.setHeightInPoints(30);
        Cell debtHeaderCell = debtHeaderRow.createCell(0);
        debtHeaderCell.setCellValue("DEBT DETAILS");
        debtHeaderCell.setCellStyle(sectionHeaderStyle);

        currentRow++;

        Row tableHeaderRow = sheet.createRow(currentRow++);
        tableHeaderRow.setHeightInPoints(25);
        String[] tableHeaders = {
                "ID", "Debtor Name", "Phone", "Original Amount",
                "Current Balance", "Paid Amount", "Debt Date", "Due Date",
                "Status", "Description"
        };

        for (int i = 0; i < tableHeaders.length; i++) {
            Cell cell = tableHeaderRow.createCell(i);
            cell.setCellValue(tableHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        List<Debt> debts = data.debts();

        for (int debtIndex = 0; debtIndex < debts.size(); debtIndex++) {
            Debt debt = debts.get(debtIndex);
            Row row = sheet.createRow(currentRow++);
            row.setHeightInPoints(20);

            CellStyle rowDataStyle = (debtIndex % 2 == 0) ? dataStyle : alternateDataStyle;

            Cell idCell = row.createCell(0);
            idCell.setCellValue(debt.getId());
            idCell.setCellStyle(rowDataStyle);

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(debt.getDebtorName());
            nameCell.setCellStyle(rowDataStyle);

            Cell phoneCell = row.createCell(2);
            phoneCell.setCellValue(debt.getDebtorPhone() != null ? debt.getDebtorPhone() : "");
            phoneCell.setCellStyle(rowDataStyle);

            Cell amountCell = row.createCell(3);
            amountCell.setCellValue(debt.getAmount().doubleValue());
            amountCell.setCellStyle(buildCurrencyDataStyle(workbook, debtIndex % 2 == 0));

            Cell balanceCell = row.createCell(4);
            double balanceValue = debt.getBalance().doubleValue();
            balanceCell.setCellValue(balanceValue);
            CellStyle balanceStyle;
            balanceStyle = buildCurrencyDataStyle(workbook, debtIndex % 2 == 0);
            if (balanceValue > 0) {
                balanceStyle = buildCurrencyNegativeDataStyle(workbook, debtIndex % 2 == 0);
            } else {
                balanceStyle = buildCurrencyPositiveDataStyle(workbook, debtIndex % 2 == 0);
            }
            balanceCell.setCellStyle(balanceStyle);

            Cell paidCell = row.createCell(5);
            paidCell.setCellValue(debt.getPaidAmount().doubleValue());
            paidCell.setCellStyle(buildCurrencyPositiveDataStyle(workbook, debtIndex % 2 == 0));

            Cell debtDateCell = row.createCell(6);
            if (debt.getDebtDate() != null) {
                debtDateCell.setCellValue(debt.getDebtDate());
                debtDateCell.setCellStyle(buildDateDataStyle(workbook, debtIndex % 2 == 0));
            } else {
                debtDateCell.setCellStyle(rowDataStyle);
            }

            Cell dueDateCell = row.createCell(7);
            if (debt.getDueDate() != null) {
                dueDateCell.setCellValue(debt.getDueDate());
                dueDateCell.setCellStyle(buildDateDataStyle(workbook, debtIndex % 2 == 0));
            } else {
                dueDateCell.setCellStyle(rowDataStyle);
            }

            Cell statusCell = row.createCell(8);
            statusCell.setCellValue(debt.getStatus());
            statusCell.setCellStyle(buildStatusStyle(workbook, debt.getStatus(), debtIndex % 2 == 0));

            Cell descCell = row.createCell(9);
            descCell.setCellValue(debt.getDescription() != null ? debt.getDescription() : "");
            descCell.setCellStyle(rowDataStyle);
        }

        int[] columnWidths = {1500, 4000, 3000, 3500, 3500, 3500, 2800, 2800, 2500, 5000};
        for (int i = 0; i < columnWidths.length && i < tableHeaders.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(19, 19, 0, 2));
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
        font.setFontHeightInPoints((short) 20);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 240, (byte) 248, (byte) 255}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorderStyle(style, new XSSFColor(new byte[]{(byte) 102, (byte) 126, (byte) 234}, null));
        return style;
    }

    private CellStyle buildSubtitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildDateHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        font.setItalic(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildSectionHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 102, (byte) 126, (byte) 234}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorderStyle(style, new XSSFColor(new byte[]{(byte) 102, (byte) 126, (byte) 234}, null));
        return style;
    }

    private CellStyle buildStatsLabelStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildStatsValueStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildTableHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 51, (byte) 65, (byte) 85}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorderStyle(style, new XSSFColor(new byte[]{(byte) 30, (byte) 41, (byte) 59}, null));
        return style;
    }

    private CellStyle buildTableDataStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildTableDataAlternateStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildCurrencyStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildCurrencyPositiveStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildCurrencyNegativeStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildPercentageStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.0%"));
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildDateStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("mm/dd/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle buildCurrencyDataStyle(XSSFWorkbook workbook, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (isEvenRow) {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        } else {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildCurrencyPositiveDataStyle(XSSFWorkbook workbook, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (isEvenRow) {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        } else {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildCurrencyNegativeDataStyle(XSSFWorkbook workbook, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (isEvenRow) {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        } else {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildDateDataStyle(XSSFWorkbook workbook, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("mm/dd/yyyy"));
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (isEvenRow) {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        } else {
            style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addLightBorderStyle(style);
        return style;
    }

    private CellStyle buildStatusStyle(XSSFWorkbook workbook, String status, boolean isEvenRow) {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);

        switch (status.toUpperCase()) {
            case "PAID":
                font.setColor(IndexedColors.WHITE.getIndex());
                style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 197, (byte) 94}, null));
                break;
            case "PENDING":
                font.setColor(IndexedColors.WHITE.getIndex());
                style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 245, (byte) 158, (byte) 11}, null));
                break;
            case "OVERDUE":
                font.setColor(IndexedColors.WHITE.getIndex());
                style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 239, (byte) 68, (byte) 68}, null));
                break;
            default:
                font.setColor(IndexedColors.DARK_BLUE.getIndex());
                if (isEvenRow) {
                    style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                } else {
                    style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 248, (byte) 250, (byte) 252}, null));
                }
        }

        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addLightBorderStyle(style);
        return style;
    }

    private void addBorderStyle(XSSFCellStyle style, XSSFColor color) {
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setTopBorderColor(color);
        style.setBottomBorderColor(color);
        style.setLeftBorderColor(color);
        style.setRightBorderColor(color);
    }

    private void addLightBorderStyle(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFColor lightGray = new XSSFColor(new byte[]{(byte) 226, (byte) 232, (byte) 240}, null);
        style.setTopBorderColor(lightGray);
        style.setBottomBorderColor(lightGray);
        style.setLeftBorderColor(lightGray);
        style.setRightBorderColor(lightGray);
    }
}