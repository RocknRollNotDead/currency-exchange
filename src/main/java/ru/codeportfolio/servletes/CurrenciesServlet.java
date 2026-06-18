package ru.codeportfolio.servletes;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.db.CurrenciesDao;
import ru.codeportfolio.db.ExchangeRatesDao;
import ru.codeportfolio.db.UserDao;
import ru.codeportfolio.services.CurrencyService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/currencies")
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

//        req.setAttribute("currencies", currenciesDao.getAllCurrencies());
        req.setAttribute("currencies", currencyService.getAllCurrencies());
        req.getRequestDispatcher("currencies.jsp").forward(req, resp);
    }

    // POST — добавить новую валюту
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String code = req.getParameter("code");
        String fullName  = req.getParameter("fullName");
        String sign = req.getParameter("sign");

//        currenciesDao.addCurrency(code, fullName, sign);
        currencyService.addCurrency(code, fullName, sign);

        // Redirect после POST — паттерн PRG (Post/Redirect/Get)
        resp.sendRedirect(req.getContextPath() + "/rates");
    }
}

