package vd.vendordebtors.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vd.vendordebtors.dao.DebtDao;
import vd.vendordebtors.dao.StatsDao;
import vd.vendordebtors.model.Vendor;
import vd.vendordebtors.model.VendorStats;
import vd.vendordebtors.model.debt.Debt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "homeServlet", value = "/")
public class HomeServlet extends HttpServlet {
    private final DebtDao debtDao = new DebtDao();
    private final StatsDao statsDao = new StatsDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("vendor") != null) {
            Vendor vendor = (Vendor) session.getAttribute("vendor");
            try {
                int page = 1;
                int size = 10;
                
                String pageParam = req.getParameter("page");
                String sizeParam = req.getParameter("size");
                
                String searchQuery = req.getParameter("searchQuery");
                String status = req.getParameter("status");
                
                if (pageParam != null && !pageParam.isEmpty()) {
                    page = Integer.parseInt(pageParam);
                }
                if (sizeParam != null && !sizeParam.isEmpty()) {
                    size = Integer.parseInt(sizeParam);
                }
                
                if (page < 1) page = 1;
                if (size < 1) size = 10;

                List<Debt> debts;
                
                boolean hasSearchParams = (searchQuery != null && !searchQuery.trim().isEmpty()) ||
                                        (status != null && !status.trim().isEmpty());
                
                if (hasSearchParams) {
                    String query = searchQuery != null && !searchQuery.trim().isEmpty() ? searchQuery.trim() : null;
                    debts = debtDao.searchDebts(
                        query,
                        query,
                        vendor.getId(),
                        status != null && !status.trim().isEmpty() ? status.trim() : null
                    );
                } else {
                    debts = debtDao.getDebtsWithPagination(vendor.getId(), page, size);
                }
                int totalDebts = debtDao.getTotalDebtsCount(vendor.getId());
                int totalPages = (int) Math.ceil((double) totalDebts / size);
                
                int startEntry = totalDebts > 0 ? ((page - 1) * size) + 1 : 0;
                int endEntry = Math.min(page * size, totalDebts);

                VendorStats stats = statsDao.getVendorStats(vendor.getId());

                req.setAttribute("vendor", vendor);
                req.setAttribute("debts", debts);
                req.setAttribute("stats", stats);
                req.setAttribute("currentPage", page);
                req.setAttribute("pageSize", size);
                req.setAttribute("totalDebts", totalDebts);
                req.setAttribute("totalPages", totalPages);
                req.setAttribute("startEntry", startEntry);
                req.setAttribute("endEntry", endEntry);
                req.setAttribute("hasPrevious", page > 1);
                req.setAttribute("hasNext", page < totalPages);
                
                req.getRequestDispatcher("home.jsp").forward(req, resp);
            } catch (SQLException | ServletException e) {
                throw new RuntimeException(e);
            }
        } else {
            resp.sendRedirect("login.jsp");
        }
    }
}
