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
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT, email TEXT);");
            stmt.execute("INSERT INTO users(name) VALUES ('Alice');");


            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS currencies (
                        id INTEGER PRIMARY KEY,
                        code VARCHAR(3) NOT NULL,
                        full_name VARCHAR(45) NOT NULL,
                        sign VARCHAR(10) NOT NULL UNIQUE
                    );
                    """);
            // в ТЗ было Currencies с заглавной буквы, но по правилам в sql используется snake_case. 45 - потому что я так чувствую, что больше не может быть, а меньше может. 10 - символа может не быть, но больше 10 это дохрена
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS exchange_rates (
                        id INTEGER PRIMARY KEY,
                        base_currency_id INTEGER NOT NULL,
                        target_currency_id INTEGER NOT NULL,
                        FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
                        FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
                        rate DECIMAL(10, 6) NOT NULL,
                        UNIQUE(base_currency_id, target_currency_id)
                    );
                    """);
            // я сделал по тз, но мне кажется не нужно для всех валют держать 6 знаков после запятой, а держать только для тех валют, где разница не существенна.

            System.out.println("База работает");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}