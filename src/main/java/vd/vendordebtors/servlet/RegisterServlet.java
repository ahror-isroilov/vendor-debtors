package vd.vendordebtors.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vd.vendordebtors.dao.VendorDao;
import vd.vendordebtors.model.Vendor;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "registerServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);
    private VendorDao vendorDao = new VendorDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRegister(req, resp);

    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Password and confirm password do not match.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }

        try {
            if (vendorDao.existsByUsername(username)) {
                req.setAttribute("error", "Username already exists. Please choose a different username.");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
                return;
            }

            if (vendorDao.existsByPhone(phone)) {
                req.setAttribute("error", "Phone number already registered. Please use a different phone number.");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
                return;
            }

            Vendor vendor = new Vendor(username.trim(), password, name.trim(), phone.trim());
            if (vendorDao.createVendor(vendor)) {
                req.setAttribute("success", "Registration successful! Please login with your credentials.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Registration failed. Please try again.");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            log.error("registration failed: {}", e.getMessage());
            req.setAttribute("error", "Registration failed. " + e.getMessage());
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
    }
}
