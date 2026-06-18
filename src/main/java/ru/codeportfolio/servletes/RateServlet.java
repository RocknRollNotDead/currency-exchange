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
import ru.codeportfolio.services.RateService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/rates")
public class RateServlet extends HttpServlet {

//    private ExchangeRatesDao exchangeRatesDao;
//    private CurrenciesDao currenciesDao;

    private RateService rateService;
    private CurrencyService currencyService;


    public void init(){
        String path = "C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db"; // пришлось захардкодить, иначе он искал в папке C:\Users\artemka\.SmartTomcat\currency-exchange\currency-exchangedatabase.db
        Connection conn;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

//        exchangeRatesDao = new ExchangeRatesDao(conn);
//        currenciesDao = new CurrenciesDao(conn);

        rateService = new RateService(conn);
        currencyService = new CurrencyService(conn);

    }
    // GET — показать список валют
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

//        req.setAttribute("rates", exchangeRatesDao.getAllExchangeRates());
//        req.setAttribute("currencies", currenciesDao.getAllCurrencies());
//
        req.setAttribute("rates", rateService.getAllExchangeRates());
        req.setAttribute("currencies", currencyService.getAllCurrencies());

        req.getRequestDispatcher("currencies.jsp").forward(req, resp);

    }

    // POST — добавить новую валюту
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int baseCurrencyId = Integer.parseInt(req.getParameter("baseCurrencyId"));
        int targetCurrencyId  = Integer.parseInt(req.getParameter("targetCurrencyId"));
        double rate = Double.parseDouble(req.getParameter("rate"));


//        exchangeRatesDao.addExchangeRate(baseCurrencyId, targetCurrencyId, rate);
        rateService.addRate(baseCurrencyId, targetCurrencyId, rate);

        // Redirect после POST — паттерн PRG (Post/Redirect/Get)
        resp.sendRedirect(req.getContextPath() + "/rates");
    }
}


