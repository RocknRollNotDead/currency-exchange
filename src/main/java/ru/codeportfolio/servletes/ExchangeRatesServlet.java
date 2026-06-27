package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.DTO.ExchangeRateDto;
import ru.codeportfolio.exceptions.*;
import ru.codeportfolio.mad.ExchangeRate;
import ru.codeportfolio.services.ExchangeRateService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

// при add отдавать обьект кого добавили

@WebServlet(urlPatterns = {"/exchangeRate/*", "/exchangeRates"})
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private DataSource dataSource;
    private final Gson gson = new Gson();
    private static final String RATE_REQUEST = "rate";

    public void init(){
        String path = "C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db"; // пришлось захардкодить, иначе он искал в папке C:\Users\artemka\.SmartTomcat\currency-exchange\currency-exchangedatabase.db

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setMaximumPoolSize(10); // сколько соединений держать одновременно

        try {
            Class.forName("org.sqlite.JDBC");
            dataSource = new HikariDataSource(config);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        exchangeRateService = new ExchangeRateService(dataSource);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // я новичок, поэтому буду обрабатывать exceptions прямо здесь

            if ("PATCH".equalsIgnoreCase(req.getMethod())) {
                doPatch(req, resp);

            } else {
                super.service(req, resp);
            }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        Gson gson = new Gson();
        String json;
        if (path == null || path.equals("/")){ // знаю про очистку от rateService.getRate("/USD/give/one/response"),
            json = gson.toJson(exchangeRateService.getAllExchangeRates()); // но в spring boot оно само это делается, а тут только код засорит
        } else {
            String request = path.substring(1);

            json = gson.toJson(exchangeRateService.getRate(request.substring(0,2), request.substring(3,5)));
        }

        resp.setStatus(HttpServletResponse.SC_OK); // 200
        resp.getWriter().write(json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode  = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        ExchangeRateDto result = exchangeRateService.addRate(baseCurrencyCode, targetCurrencyCode, rate);

        if (result != null){
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
        }

        String json = gson.toJson(result);
        resp.getWriter().write(json);

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        String body = req.getReader().lines().collect(Collectors.joining());
        String[] parts = body.split("=");
        String rate;

        if (parts[0].equals(RATE_REQUEST)){
            rate = parts[1];
        } else {
            throw new UncorrectRequestException("Expected: \"" + RATE_REQUEST + "\"=\"123\""); // выглядит как "rate"="123"
        }

        String request = path.substring(1);

        String baseCurrencyCode = request.substring(0,3);
        String targetCurrencyCode = request.substring(3,6);

        ExchangeRateDto result = exchangeRateService.changeRate(baseCurrencyCode, targetCurrencyCode, rate);

        if (result != null){
            resp.setStatus(HttpServletResponse.SC_OK); // 200
        }
        String jsonObj = gson.toJson(result);
        resp.getWriter().write(jsonObj);

    }

    @Override
    public void destroy() {
        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }

}


