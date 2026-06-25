package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.exceptions.*;
import ru.codeportfolio.mad.ExchangeRate;
import ru.codeportfolio.services.RateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

// при add отдавать обьект кого добавили

@WebServlet(urlPatterns = {"/exchangeRate/*", "/exchangeRates"})
public class RateServlet extends HttpServlet {

    private RateService rateService;
    private final Gson gson = new Gson();

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

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // я новичок, поэтому буду обрабатывать exceptions прямо здесь

        String json;

        try {
            if ("PATCH".equalsIgnoreCase(req.getMethod())) {
                doPatch(req, resp);

            } else {
                super.service(req, resp);
            }
        }  catch (NotFoundException e){
            sendException(resp, e, HttpServletResponse.SC_NOT_FOUND); // 404

        } catch (ValidationException e){
            sendException(resp, e, HttpServletResponse.SC_BAD_REQUEST); // 400

        } catch (AlreadyExistException | SelfRatingException e){
            sendException(resp, e, HttpServletResponse.SC_CONFLICT); // 409

        } catch (DataAccessException | IOException e){
            sendException(resp, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500

        }

    }

    private void sendException(HttpServletResponse resp, Exception e, int httpCode){
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(httpCode);
        String json = gson.toJson(Map.of("message", e.getMessage()));
        try (PrintWriter writer = resp.getWriter() ){
            writer.write(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        Gson gson = new Gson();
        String json;
        if (path == null || path.equals("/")){ // знаю про очистку от /USD/give/one/response, но в spring boot оно само это делается, а тут только код засорит
            json = gson.toJson(rateService.getAllExchangeRates());
        } else {
            String request = path.substring(1);

            json = gson.toJson(rateService.getRate(request.substring(0,2), request.substring(3,5)));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.setStatus(HttpServletResponse.SC_OK); // 200
        resp.getWriter().write(json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        Gson gson = new Gson();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode  = req.getParameter("targetCurrencyCode");
        double rate = Double.parseDouble(req.getParameter("rate"));

        ExchangeRate result = rateService.addRate(baseCurrencyCode, targetCurrencyCode, rate);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (result != null){
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
        }

        String json = gson.toJson(result);
        resp.getWriter().write(json);

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
//        Gson gson = new Gson();

//        String body = reader.lines().collect(Collectors.joining());
        String body = req.getReader().lines().collect(Collectors.joining());
        String[] parts = body.split("=");

        double rate;
        try{
            rate = Double.parseDouble(parts[1]);
        }catch (NullPointerException e) {
            throw new ValidationException("must be not null");
        }

        String request = path.substring(1);

        String baseCurrencyCode = request.substring(0,3);
        String targetCurrencyCode = request.substring(3,6);

        ExchangeRate result = rateService.changeRate(baseCurrencyCode, targetCurrencyCode, rate);

        if (result != null){
            resp.setStatus(HttpServletResponse.SC_OK); // 200
        }
        String jsonObj = gson.toJson(result);
        resp.getWriter().write(jsonObj);

    }

}


