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
        dataSource = (DataSource) getServletContext().getAttribute("dataSource");
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

}
