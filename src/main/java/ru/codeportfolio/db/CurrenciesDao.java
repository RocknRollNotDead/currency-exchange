package ru.codeportfolio.db;


import ru.codeportfolio.exceptions.DataAccessException;
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
            throw new DataAccessException("Failed to fetch currencies", e);
        }
    }

    public int addCurrency(String code, String fullName, String sign) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO currencies(code, full_name, sign) VALUES (?, ?, ?);"
        )){

            stmt.setString(1, code);
            stmt.setString(2, fullName);
            stmt.setString(3, sign);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add currency", e);
        }
    }

    public Currency findByCode(String code){

        try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM currencies WHERE code = ?"
            )){
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
            throw new DataAccessException("Failed to get currency", e);
        }
    }

    public Currency findBySign(String sign){

        try
            (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM currencies WHERE sign = ?"
            )){
            stmt.setString(1, sign);
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
            throw new DataAccessException("Failed to get currency", e);
        }
    }

    public Currency findById(int id) {

        try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM currencies WHERE id = ?"
            )){
            stmt.setInt(1, id);
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
            throw new DataAccessException("Failed to get currency", e);
        }
    }

    public int updateCurrency(String code, String fullName, String sign) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE currencies SET full_name = ?, sign = ? WHERE code = ?;"
        )){

            stmt.setString(1, fullName);
            stmt.setString(2, sign);
            stmt.setString(3, code);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update currency", e);
        }
    }

    public int deleteCurrency(String code) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM currencies WHERE code = ?"
        )){

            stmt.setString(1, code);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete currency", e);
        }
    }



}

