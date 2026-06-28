package ru.codeportfolio.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.DTO.ExchangeRateDto;
import ru.codeportfolio.exceptions.*;
import ru.codeportfolio.services.ExchangeRateService;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.stream.Collectors;

// при add отдавать обьект кого добавили

@WebServlet(urlPatterns = {"/exchangeRate/*", "/exchangeRates"})
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private final Gson gson = new Gson();
    private static final String RATE_REQUEST = "rate";

    public void init(){
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");

        exchangeRateService = new ExchangeRateService(dataSource);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

            if ("PATCH".equalsIgnoreCase(req.getMethod())) {
                doPatch(req, resp);

            } else {
                super.service(req, resp);
            }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String path = req.getPathInfo();
        Gson gson = new Gson();
        String json;
        String request;
        if (servletPath.equals("/exchangeRates")) { // знаю про очистку от rateService.getRate("/USD/give/one/response"),
            json = gson.toJson(exchangeRateService.getAllExchangeRates()); // но в spring boot оно само это делается, а тут только код засорит

        } else {

            try{
                request = path.substring(1);
            } catch (NullPointerException e){
                throw new UncorrectRequestException("request must be not null");
            }
            try{
                json = gson.toJson(exchangeRateService.getRate(request.substring(0, 3), request.substring(3, 6)));
            } catch (StringIndexOutOfBoundsException e) {
                throw new UncorrectRequestException("Uncorrect request. Request must be \"USDEUR\", for example", e);
            }

        }

        resp.setStatus(HttpServletResponse.SC_OK); // 200
        resp.getWriter().write(json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // POST запрос на rateS для одной валюты это не очень логично. Поэтому пускай на обоих путях будет такое.
        // я, как юзер, не ожидаю, что надо передавать POST запрос одного курса на курсЫ
        // поэтому должна быть возможность послать пост запрос с ОДНИМ курсом на path КУРС, а не курсЫ

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
            try{
                rate = parts[1];
            } catch (IndexOutOfBoundsException e) {
                throw new ValidationException("Input must be not empty");
            }

        } else {
            throw new UncorrectRequestException("Expected: \"" + RATE_REQUEST + "\"=\"123\""); // выглядит как "rate"="123"
        } // надеюсь в Spring вместо этих 13 строчек есть метод на одну строчку - getParametr("rate") и всё.


        String request = path.substring(1);
        ExchangeRateDto result;
        try{
            String baseCurrencyCode = request.substring(0,3);
            String targetCurrencyCode = request.substring(3,6); // в рамках учебного проекта опустим возможность запросов /USDABCDEFGCYUEUHUHU.
            result = exchangeRateService.changeRate(baseCurrencyCode, targetCurrencyCode, rate);
        } catch (NullPointerException e) {
             throw new UncorrectRequestException("Uncorrect request. Request must be \"USDEUR\", for example", e);
        }




        if (result != null){
            resp.setStatus(HttpServletResponse.SC_OK); // 200
        }
        String jsonObj = gson.toJson(result);
        resp.getWriter().write(jsonObj);

    }

}


