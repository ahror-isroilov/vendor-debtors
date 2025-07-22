package vd.vendordebtors.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Locale;

public class MyAppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Locale.setDefault(Locale.US);
    }
}
