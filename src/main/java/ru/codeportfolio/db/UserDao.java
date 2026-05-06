package ru.codeportfolio.db;


import ru.codeportfolio.mad.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    // Имитация базы данных
    private static List<User> users = new ArrayList<>();
    private static int nextId = 1;

    static {
        users.add(new User(nextId++, "Иван", "ivan@mail.ru"));
        users.add(new User(nextId++, "Мария", "maria@mail.ru"));
    }

    public List<User> getAllUsers() {
        return users;
    }

    public void addUser(String name, String email) {
        users.add(new User(nextId++, name, email));
    }
}
