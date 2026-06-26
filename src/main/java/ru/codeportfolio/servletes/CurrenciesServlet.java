package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.DTO.CurrencyDto;
import ru.codeportfolio.exceptions.*;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.services.CurrencyService;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(urlPatterns = {"/currency/*", "/currencies"})
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;
    private DataSource dataSource;
    private final Gson gson = new Gson();

    public void init(){
        String path = "C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setMaximumPoolSize(10); // сколько соединений держать одновременно

        try {
            Class.forName("org.sqlite.JDBC");
            dataSource = new HikariDataSource(config);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        currencyService = new CurrencyService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();

        String json;

        if (path == null || path.equals("/")){
            json = gson.toJson(currencyService.getAllCurrencies());
        } else {
            String code = path.substring(1);
            json = gson.toJson(currencyService.getCurrency(code));
        }

        resp.setStatus(HttpServletResponse.SC_OK); // 200
        resp.getWriter().write(json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String code = req.getParameter("code");
        String name  = req.getParameter("name");
        String sign = req.getParameter("sign");

        CurrencyDto result = currencyService.addCurrency(code, name, sign);


        if (result != null){
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
        }

        String json = gson.toJson(result);
        resp.getWriter().write(json);

    }

    @Override
    public void destroy() {
        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }
}

