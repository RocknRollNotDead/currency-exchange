package ru.codeportfolio.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FirstDbApplication {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:database.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS currencies (
                        id INTEGER PRIMARY KEY,
                        code VARCHAR(3) NOT NULL,
                        full_name VARCHAR(45) NOT NULL,
                        sign VARCHAR(1) UNIQUE
                    );
                    """);

            System.out.println("База работает");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}