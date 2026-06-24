package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.services.CurrencyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/currencies/*")
public class CurrenciesServlet extends HttpServlet {

//    private CurrenciesDao currenciesDao;
    private CurrencyService currencyService;

    public void init(){
        String path = "C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db";
        Connection conn;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

//        currenciesDao = new CurrenciesDao(conn);
        currencyService = new CurrencyService(conn);
    }
    // GET — показать список валют
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        Gson gson = new Gson();
        String json;

        if (path == null || path.equals("/")){
            json = gson.toJson(currencyService.getAllCurrencies());
        } else {
            String code = path.substring(1);
            json = gson.toJson(currencyService.getCurrency(code));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.getWriter().write(json);

    }

    // POST — добавить новую валюту
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Gson gson = new Gson();

        String code = req.getParameter("code");
        String name  = req.getParameter("name");
        String sign = req.getParameter("sign");

        Currency result = currencyService.addCurrency(code, name, sign);

        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");

        String json = gson.toJson(result);

        resp.getWriter().write(json);

    }
}

