package ru.codeportfolio.db;


import ru.codeportfolio.mad.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//ор
public class UserDao {
    private final Connection conn;

    public UserDao(Connection conn) {
        this.conn = conn;
    }

    public List<User> getAllUsers() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users")){
            List<User> users = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"));
                users.add(user);
            }
            System.out.println(users);
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(String name, String email) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users(name, email) VALUES (?, ?);"
        )){

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            System.out.println("создался юзер " + name + " " + email);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }



    public User findByEmail(String email){

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE email = ?"
            );
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return new User(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
