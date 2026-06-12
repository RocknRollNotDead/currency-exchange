package ru.codeportfolio.db;


import ru.codeportfolio.mad.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// добавить
// удалить по коду
// показать все
// показать по коду
// поменять по коду (для галочки, использовать я её не буду)

public class CurrenciesDao {
    private final Connection conn;

    public CurrenciesDao(Connection conn) {
        this.conn = conn;
    }

    public List<Currency> getAllCurrencies() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM currencies")){
            List<Currency> currencies = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Currency currency = new Currency(rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("full_name"),
                        rs.getString("sign") );
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCurrency(String code, String fullName, String sign) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO currencies(code, full_name, sign) VALUES (?, ?, ?);"
        )){

            stmt.setString(1, code);
            stmt.setString(2, fullName);
            stmt.setString(3, sign);
            stmt.executeUpdate();
            System.out.println("создалася валюта " + code + " " + fullName + " " + sign);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency findByCode(String code){

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM currencies WHERE code = ?"
            );
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Currency(rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("full_name"),
                        rs.getString("sign")

                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*public Currencies findBySign(String sign){

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM currencies WHERE sign = ?"
            );
            stmt.setString(1, sign);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Currencies(rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("full_name"),
                        rs.getString("sign")

                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/



    public void updateCurrency(String code, String fullName, String sign) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE INTO currencies(code, full_name, sign) VALUES (?, ?, ?);"
        )){

            stmt.setString(1, code);
            stmt.setString(2, fullName);
            stmt.setString(3, sign);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCurrency(String code) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM currencies WHERE code = ?"
        )){

            stmt.setString(1, code);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





}

