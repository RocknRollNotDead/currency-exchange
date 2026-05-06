package ru.codeportfolio.servletes;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.codeportfolio.db.UserDao;

import java.io.IOException;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    // GET — показать список пользователей
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("users", userDao.getAllUsers());
        req.getRequestDispatcher("users.jsp").forward(req, resp);
    }

    // POST — добавить нового пользователя
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name  = req.getParameter("name");
        String email = req.getParameter("email");

        userDao.addUser(name, email);

        // Redirect после POST — паттерн PRG (Post/Redirect/Get)
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}
