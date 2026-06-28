package ru.codeportfolio.servlets.other;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.codeportfolio.exceptions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebFilter (urlPatterns = {"/currency/*", "/currencies", "/exchangeRate/*", "/exchangeRates", "/exchange"})
public class CurrencyExchangeFilter implements Filter {


    private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        servletRequest.setCharacterEncoding("UTF-8");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (NotFoundException e){
            sendException(resp, e, HttpServletResponse.SC_NOT_FOUND); // 404

        } catch (ValidationException | UncorrectRequestException e){
            sendException(resp, e, HttpServletResponse.SC_BAD_REQUEST); // 400

        } catch (AlreadyExistException | SelfRatingException e){
            sendException(resp, e, HttpServletResponse.SC_CONFLICT); // 409

        } catch (DataAccessException e){
            System.err.println(e);
            Exception myException = new Exception("Database error", e);
            sendException(resp, myException, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            System.err.println(e);
            Exception myException = new Exception("Server error", e);
            sendException(resp, myException, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }





    }

    private void sendException(HttpServletResponse resp, Exception e, int httpCode){
        Gson gson = new Gson();

        resp.setStatus(httpCode);
        String json = gson.toJson(Map.of("message", e.getMessage()));
        try (PrintWriter writer = resp.getWriter() ){
            writer.write(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
