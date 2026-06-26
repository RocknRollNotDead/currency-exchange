package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.exceptions.*;
import ru.codeportfolio.DTO.ExchangeDto;
import ru.codeportfolio.services.ExchangeRateService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private DataSource dataSource;
    Gson gson = new Gson();

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        String json;

        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountParam = req.getParameter("amount");
        // todo validate this

        BigDecimal value;
        value = new BigDecimal(amountParam);

        ExchangeDto result = exchangeRateService.calculateRate(baseCurrencyCode, targetCurrencyCode, value);

        json = gson.toJson(result);

        resp.getWriter().write(json);

    }

    @Override
    public void destroy() {
        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }




}
