package ru.codeportfolio.servletes;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.exceptions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebFilter ("/*")
public class CurrencyExchangeFilter implements Filter {


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

        } catch (ValidationException e){
            sendException(resp, e, HttpServletResponse.SC_BAD_REQUEST); // 400

        } catch (AlreadyExistException | SelfRatingException e){
            sendException(resp, e, HttpServletResponse.SC_CONFLICT); // 409

        } catch (DataAccessException  e){
            sendException(resp, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        } catch (NumberFormatException e){
            sendException(resp, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        } catch (Exception e){
            sendException(resp, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
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
