package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.exceptions.*;
import ru.codeportfolio.mad.Exchange;
import ru.codeportfolio.services.RateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private RateService rateService;
    Gson gson = new Gson();

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

        try {
            super.service(req, resp);

        }   catch (NotFoundException e){
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

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json;

        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountParam = req.getParameter("amount");
        // todo validate this

        double value;
        value = Double.parseDouble(amountParam);

        Exchange result = rateService.calculateRate(baseCurrencyCode, targetCurrencyCode, value);

        json = gson.toJson(result);

        resp.getWriter().write(json);

    }






}
