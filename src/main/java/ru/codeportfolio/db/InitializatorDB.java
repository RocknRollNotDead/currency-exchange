package ru.codeportfolio.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitializatorDB {
    public static void execute(){

        String url = "jdbc:sqlite:C:/Users/artemka/Documents/pet-projects/currency-exchange/database.db";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {



            stmt.execute("DROP TABLE users");
            stmt.execute("DROP TABLE currencies");
            stmt.execute("DROP TABLE exchange_rates");

            stmt.execute("PRAGMA foreign_keys = ON;");

//            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT, email TEXT);");
//            stmt.execute("INSERT INTO users(name, email) VALUES ('Alice', 'mail example');");


            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS currencies (
                        id INTEGER PRIMARY KEY,
                        code VARCHAR(3) NOT NULL UNIQUE CHECK (length(code) = 3 AND code GLOB '[A-Z][A-Z][A-Z]'),
                        full_name VARCHAR(45) NOT NULL,
                        sign VARCHAR(2) NOT NULL CHECK (length(code) = 3)
                    );
                    """);
            // 45 - потому что я так чувствую, что больше не может быть, а меньше может. символа может не быть, yes null
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS exchange_rates (
                        id INTEGER PRIMARY KEY,
                        base_currency_id INTEGER NOT NULL,
                        target_currency_id INTEGER NOT NULL,
                        rate DECIMAL(10, 6) NOT NULL,
                        FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
                        FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
                        UNIQUE(base_currency_id, target_currency_id)
                    );
                    """);

            System.out.println("База работает");


//            Class.forName("org.sqlite.JDBC");
//            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            //todo обработать все ошибки создания
            e.printStackTrace();
        }
    }
}
