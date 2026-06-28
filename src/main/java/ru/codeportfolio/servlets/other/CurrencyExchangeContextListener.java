package ru.codeportfolio.servlets.other;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.codeportfolio.db.InitializatorDB;

@WebListener
public class CurrencyExchangeContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();


        String path = System.getenv("DB_PATH");

        if (path == null) {
            path = context.getInitParameter("db.path");
        }
        if (path == null) {
            throw new IllegalStateException("Не задан context-param db.path или DB_PATH в .env");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setMaximumPoolSize(10);
        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        HikariDataSource dataSource = new HikariDataSource(config);
        InitializatorDB.execute(dataSource);
        context.setAttribute("dataSource", dataSource);


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Object dataSource = sce.getServletContext().getAttribute("dataSource");
        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }
}
