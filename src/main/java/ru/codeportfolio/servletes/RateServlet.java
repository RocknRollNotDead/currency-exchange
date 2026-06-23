package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.db.CurrenciesDao;
import ru.codeportfolio.db.ExchangeRatesDao;
import ru.codeportfolio.db.UserDao;
import ru.codeportfolio.mad.CreateRateRequest;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.mad.ExchangeRate;
import ru.codeportfolio.services.CurrencyService;
import ru.codeportfolio.services.RateService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class RateServlet extends HttpServlet {

//    private ExchangeRatesDao exchangeRatesDao;
//    private CurrenciesDao currenciesDao;

    private RateService rateService;


    public void init(){
        String path = "C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db"; // пришлось захардкодить, иначе он искал в папке C:\Users\artemka\.SmartTomcat\currency-exchange\currency-exchangedatabase.db
        Connection conn;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        rateService = new RateService(conn);
    }
    // GET — показать список валют
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        Gson gson = new Gson();
        String json;
        if (path == null || path.equals("/")){
//            req.setAttribute("currencies", currencyService.getAllCurrencies());
            json = gson.toJson(rateService.getAllExchangeRates());
        } else { // one currency
            String request = path.substring(1);

            json = gson.toJson(rateService.getRate(request.substring(0,2), request.substring(3,5)));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.getWriter().write(json);

//        req.setAttribute("rates", exchangeRatesDao.getAllExchangeRates());
//        req.setAttribute("currencies", currenciesDao.getAllCurrencies());
//
//        req.setAttribute("rates", rateService.getAllExchangeRates());
//        req.setAttribute("currencies", currencyService.getAllCurrencies());
//
//        req.getRequestDispatcher("currencies.jsp").forward(req, resp);



    }

    // POST — добавить новую валюту
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode  = req.getParameter("targetCurrencyCode");
        double rate = Double.parseDouble(req.getParameter("rate"));


        req.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();

        CreateRateRequest createRateRequest = gson.fromJson(req.getReader(), CreateRateRequest.class);

        rateService.addRate(
                createRateRequest.baseCurrencyCode,
                createRateRequest.targetCurrencyCode,
                createRateRequest.rate
        );

        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"created\"}");

//        exchangeRatesDao.addExchangeRate(baseCurrencyId, targetCurrencyId, rate);
//        rateService.addRate(baseCurrencyCode, targetCurrencyCode, rate);

        // Redirect после POST — паттерн PRG (Post/Redirect/Get)
//        resp.sendRedirect(req.getContextPath() + "/rates");
    }
}


