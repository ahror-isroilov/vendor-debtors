package vd.vendordebtors.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vd.vendordebtors.dao.VendorDao;
import vd.vendordebtors.model.Vendor;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    private VendorDao vendorDao = new VendorDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleLogin(req, resp);

    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            Vendor vendor = vendorDao.authenticate(username, password);
            if (vendor != null) {
                HttpSession session = req.getSession();
                session.setAttribute("vendor", vendor);
                session.setMaxInactiveInterval(60 * 30);
                resp.sendRedirect("/");
            } else {
                req.setAttribute("error", "Invalid username or password");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Login failed.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
