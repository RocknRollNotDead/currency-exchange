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
//      all currencies
        if (path == null || path.equals("/")){
//            req.setAttribute("currencies", currencyService.getAllCurrencies());
            json = gson.toJson(currencyService.getAllCurrencies());
        } else { // one currency
            String code = path.substring(1);
//            req.setAttribute("currency", currencyService.getCurrency(code));
            json = gson.toJson(currencyService.getCurrency(code));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.getWriter().write(json);




//        req.getRequestDispatcher("/currencies.jsp").forward(req, resp);

    }
    /*
    *
    * <h2>Посмотреть валюту</h2>
        <form method="GET" action="${pageContext.request.contextPath}/currencies">
            Код:            <input type="text" name="code"/>  <br/>
            <button type="submit">Посмотреть</button>
        </form>
        *     <h2>Посмотреть курс</h2>
        <form method="POST" action="/rate">
            Базовая валюта:     <input type="text" name="baseCurrencyCode"/>  <br/>
            Целевая валюта:     <input type="text" name="targetCurrencyCode"/> <br/>

            <button type="submit">Посмотреть</button>
        </form>

        * */

    // POST — добавить новую валюту
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        BufferedReader reader = req.getReader();


        req.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();

        Currency currency = gson.fromJson(req.getReader(), Currency.class);

        currencyService.addCurrency(
                currency.getCode(),
                currency.getName(),
                currency.getSign()
        );

        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"created\"}");


//        String code = req.getParameter("code");
//        String fullName  = req.getParameter("fullName");
//        String sign = req.getParameter("sign");

//        currenciesDao.addCurrency(code, fullName, sign);
//        currencyService.addCurrency(code, fullName, sign);
//
//        // Redirect после POST — паттерн PRG (Post/Redirect/Get)
//        resp.sendRedirect(req.getContextPath() + "/rates");
    }
}

