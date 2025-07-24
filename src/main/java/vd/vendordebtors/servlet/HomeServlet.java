package vd.vendordebtors.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vd.vendordebtors.dao.DebtDao;
import vd.vendordebtors.dao.StatsDao;
import vd.vendordebtors.model.DetailedStats;
import vd.vendordebtors.model.Vendor;
import vd.vendordebtors.model.VendorStats;
import vd.vendordebtors.model.debt.Debt;
import vd.vendordebtors.util.PageResult;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "homeServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    private final DebtDao debtDao = new DebtDao();
    private final StatsDao statsDao = new StatsDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Vendor vendor = (Vendor) session.getAttribute("vendor");
        
        try {
            PageResult pageResult = getPageResult(req, vendor);

            VendorStats stats = statsDao.getVendorStats(vendor.getId());

            DetailedStats detailedStats = getDetailedStats(req, resp, pageResult.statsStartDate(), pageResult.statsEndDate(), vendor);

            req.setAttribute("vendor", vendor);
            req.setAttribute("detailedStats", detailedStats);
            req.setAttribute("debts", pageResult.debts());
            req.setAttribute("stats", stats);
            req.setAttribute("currentPage", pageResult.page());
            req.setAttribute("pageSize", pageResult.size());
            req.setAttribute("totalDebts", pageResult.totalDebts());
            req.setAttribute("totalPages", pageResult.totalPages());
            req.setAttribute("startEntry", pageResult.startEntry());
            req.setAttribute("endEntry", pageResult.endEntry());
            req.setAttribute("hasPrevious", pageResult.page() > 1);
            req.setAttribute("hasNext", pageResult.page() < pageResult.totalPages());

            req.getRequestDispatcher("home.jsp").forward(req, resp);
        } catch (SQLException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private PageResult getPageResult(HttpServletRequest req, Vendor vendor) throws SQLException {
        int page = 1;
        int size = 10;

        String pageParam = req.getParameter("page");
        String sizeParam = req.getParameter("size");

        String searchQuery = req.getParameter("searchQuery");
        String status = req.getParameter("status");
        String statsStartDate = req.getParameter("statsStartDate");
        String statsEndDate = req.getParameter("statsEndDate");

        if (pageParam != null && !pageParam.isEmpty()) {
            page = Integer.parseInt(pageParam);
        }
        if (sizeParam != null && !sizeParam.isEmpty()) {
            size = Integer.parseInt(sizeParam);
        }

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        List<Debt> debts;
        int totalDebts;

        boolean hasSearchParams = (searchQuery != null && !searchQuery.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty());

        if (hasSearchParams) {
            String query = searchQuery != null && !searchQuery.trim().isEmpty() ? searchQuery.trim() : null;
            String statusFilter = status != null && !status.trim().isEmpty() ? status.trim() : null;

            List<Debt> allFilteredDebts = debtDao.searchDebts(query, query, vendor.getId(), statusFilter);
            totalDebts = allFilteredDebts.size();

            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, totalDebts);

            if (fromIndex < totalDebts) {
                debts = allFilteredDebts.subList(fromIndex, toIndex);
            } else {
                debts = java.util.Collections.emptyList();
            }
        } else {
            debts = debtDao.getDebtsWithPagination(vendor.getId(), page, size);
            totalDebts = debtDao.getTotalDebtsCount(vendor.getId());
        }

        int totalPages = (int) Math.ceil((double) totalDebts / size);

        int startEntry = totalDebts > 0 ? ((page - 1) * size) + 1 : 0;
        int endEntry = Math.min(page * size, totalDebts);
        return new PageResult(page, size, statsStartDate, statsEndDate, debts, totalDebts, totalPages, startEntry, endEntry);
    }

    private DetailedStats getDetailedStats(HttpServletRequest req, HttpServletResponse resp, String statsStartDate, String statsEndDate, Vendor vendor) {
        DetailedStats detailedStats = null;
        if (statsStartDate != null && statsEndDate != null && !statsStartDate.isEmpty() && !statsEndDate.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(statsStartDate);
                Date endDate = dateFormat.parse(statsEndDate);
                detailedStats = statsDao.getDetailedStatsByDate(vendor.getId(), startDate, endDate);
            } catch (Exception e) {
                req.setAttribute("error", "Error occurred while loading detailed stats: " + e.getMessage());
            }
        } else {
            try {
                Date today = new Date();
                Date thirtyDaysAgo = new Date(today.getTime() - (30L * 24 * 60 * 60 * 1000));
                detailedStats = statsDao.getDetailedStatsByDate(vendor.getId(), thirtyDaysAgo, today);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                req.setAttribute("defaultStartDate", dateFormat.format(thirtyDaysAgo));
                req.setAttribute("defaultEndDate", dateFormat.format(today));
            } catch (Exception e) {
                req.setAttribute("error", "Error occurred while loading detailed stats: " + e.getMessage());
            }
        }
        return detailedStats;
    }
}
