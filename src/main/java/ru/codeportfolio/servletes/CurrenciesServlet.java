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
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.services.CurrencyService;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(urlPatterns = {"/currency/*", "/currencies"})
public class CurrenciesServlet extends HttpServlet {

//    private CurrenciesDao currenciesDao;
    private CurrencyService currencyService;
    private DataSource dataSource;
    private final Gson gson = new Gson();

    public void init(){
        String path = "C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setMaximumPoolSize(10); // сколько соединений держать одновременно

        dataSource = new HikariDataSource(config);
        Connection conn;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        currencyService = new CurrencyService(conn);
    }
    // GET — показать список валют

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try{
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

        String path = req.getPathInfo();

        String json;

        if (path == null || path.equals("/")){
            json = gson.toJson(currencyService.getAllCurrencies());
        } else {
            String code = path.substring(1);
            json = gson.toJson(currencyService.getCurrency(code));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.setStatus(HttpServletResponse.SC_OK); // 200
        resp.getWriter().write(json);

    }

    // POST — добавить новую валюту
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String code = req.getParameter("code");
        String name  = req.getParameter("name");
        String sign = req.getParameter("sign");

        Currency result = currencyService.addCurrency(code, name, sign);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (result != null){
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
        }

        String json = gson.toJson(result);
        resp.getWriter().write(json);

    }

    @Override
    public void destroy() {
        if (dataSource instanceof HikariDataSource hikari) {
            hikari.close();
        }
    }
}

