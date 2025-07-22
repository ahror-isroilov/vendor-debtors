package vd.vendordebtors.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vd.vendordebtors.dao.DebtDao;
import vd.vendordebtors.model.Vendor;
import vd.vendordebtors.model.debt.Debt;

import java.io.IOException;

@WebServlet(name = "debtServlet", value = "/debt")
public class DebtServlet extends HttpServlet {
    private final DebtDao debtDao = new DebtDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            createDebt(req, resp);
        } else if ("edit".equals(action)) {
            updateDebt(req, resp);
        } else if ("delete".equals(action)) {
            deleteDebt(req, resp);
        }
    }

    private void createDebt(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Vendor vendor = (Vendor) req.getSession().getAttribute("vendor");
            if (vendor == null) {
                resp.sendRedirect("login.jsp");
                return;
            }
            int vendorId = vendor.getId();

            String debtorName = req.getParameter("debtorName");
            String debtorPhone = req.getParameter("debtorPhone");
            String amountStr = req.getParameter("amount");
            String debtDateStr = req.getParameter("debtDate");
            String dueDateStr = req.getParameter("dueDate");
            String description = req.getParameter("description");

            if (debtorName == null || debtorName.trim().isEmpty() ||
                    amountStr == null || debtDateStr == null) {
                req.setAttribute("error", "Required fields are missing");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
                return;
            }

            Debt debt = new Debt();
            debt.setVendorId(vendorId);
            debt.setDebtorName(debtorName.trim());
            debt.setDebtorPhone(debtorPhone != null ? debtorPhone.trim() : null);
            java.math.BigDecimal amount = new java.math.BigDecimal(amountStr);
            if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                req.setAttribute("error", "Amount must be greater than 0");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
                return;
            }
            debt.setAmount(amount);
            debt.setBalance(amount);
            debt.setDescription(description != null ? description.trim() : null);
            debt.setDebtDate(java.sql.Date.valueOf(debtDateStr));

            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                debt.setDueDate(java.sql.Date.valueOf(dueDateStr));
            }

            debt.setCreatedDate(new java.util.Date());
            debt.setStatus("PENDING");

            boolean success = debtDao.createDebt(debt);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                req.setAttribute("error", "Failed to create debt");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            req.setAttribute("error", "Error occurred while creating debt: " + e.getMessage());
            req.getRequestDispatcher("home.jsp").forward(req, resp);
        }
    }

    private void updateDebt(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Vendor vendor = (Vendor) req.getSession().getAttribute("vendor");
            if (vendor == null) {
                resp.sendRedirect("login.jsp");
                return;
            }
            int vendorId = vendor.getId();

            String debtIdStr = req.getParameter("debtId");
            String debtorName = req.getParameter("debtorName");
            String debtorPhone = req.getParameter("debtorPhone");
            String amountStr = req.getParameter("amount");
            String balanceStr = req.getParameter("balance");
            String debtDateStr = req.getParameter("debtDate");
            String dueDateStr = req.getParameter("dueDate");
            String status = req.getParameter("status");
            String description = req.getParameter("description");

            if (debtIdStr == null || debtorName == null || debtorName.trim().isEmpty() ||
                    amountStr == null || balanceStr == null || debtDateStr == null) {
                req.setAttribute("error", "Required fields are missing");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
                return;
            }

            Debt debt = new Debt();
            debt.setId(Integer.parseInt(debtIdStr));
            debt.setVendorId(vendorId);
            debt.setDebtorName(debtorName.trim());
            debt.setDebtorPhone(debtorPhone != null ? debtorPhone.trim() : null);
            debt.setAmount(new java.math.BigDecimal(amountStr));
            debt.setBalance(new java.math.BigDecimal(balanceStr));
            debt.setDescription(description != null ? description.trim() : null);
            debt.setDebtDate(java.sql.Date.valueOf(debtDateStr));

            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                debt.setDueDate(java.sql.Date.valueOf(dueDateStr));
            }

            debt.setStatus(status != null ? status : "ACTIVE");

            boolean success = debtDao.updateDebt(debt);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                req.setAttribute("error", "Failed to update debt");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            req.setAttribute("error", "Error occurred while updating debt: " + e.getMessage());
            req.getRequestDispatcher("home.jsp").forward(req, resp);
        }
    }

    private void deleteDebt(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Vendor vendor = (Vendor) req.getSession().getAttribute("vendor");
            if (vendor == null) {
                resp.sendRedirect("login.jsp");
                return;
            }
            int vendorId = vendor.getId();

            String debtIdStr = req.getParameter("debtId");

            if (debtIdStr == null || debtIdStr.trim().isEmpty()) {
                req.setAttribute("error", "Debt ID is required");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
                return;
            }

            int debtId = Integer.parseInt(debtIdStr);
            boolean success = debtDao.deleteDebt(debtId, vendorId);

            if (success) {
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                req.setAttribute("error", "Failed to delete debt");
                req.getRequestDispatcher("home.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            req.setAttribute("error", "Error occurred while deleting debt: " + e.getMessage());
            req.getRequestDispatcher("home.jsp").forward(req, resp);
        }
    }

}
