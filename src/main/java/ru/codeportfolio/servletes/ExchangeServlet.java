package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.services.RateService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        Gson gson = new Gson();
        String json;
        if (path == null || path.equals("/")){
            json = gson.toJson(rateService.getAllExchangeRates());
        } else {
            String request = path.substring(1);

            json = gson.toJson(rateService.getRate(request.substring(0,2), request.substring(3,5)));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        resp.getWriter().write(json);

    }






}
