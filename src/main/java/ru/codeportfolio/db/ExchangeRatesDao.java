package ru.codeportfolio.db;

import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.mad.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// добавить
// удалить
// поменять значение курса
// показать все
// показать один по id двух валют



public class ExchangeRatesDao {

    private final Connection conn;

    public ExchangeRatesDao(Connection conn) {
        this.conn = conn;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM exchange_rates")){
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExchangeRate exchangeRate = new ExchangeRate(rs.getInt("id"),
                        rs.getInt("base_currency_id"),
                        rs.getInt("target_currency_id"),
                        rs.getDouble("rate") );
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch rates", e);
        }
    }

    public void addExchangeRate(int baseCurrencyId, int targetCurrencyId, double rate) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate) VALUES (?, ?, ?);"
        )){

            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            stmt.setDouble(3, rate);
            stmt.executeUpdate();
            System.out.println("создался курс " + baseCurrencyId + " " + targetCurrencyId + " " + rate);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add rate", e);
        }
    }

    public void deleteRate(int id){
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM exchange_rates WHERE id = ?;"
        )){

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete rate", e);
        }
    }

    public void deleteRate(int baseCurrencyId, int targetCurrencyId){
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?;"
        )){

            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete rate", e);
        }
    }


    public ExchangeRate findByBaseCurrencyIdAndTargetCurrencyId(int baseCurrencyId, int targetCurrencyId){

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?"
            );
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ExchangeRate(rs.getInt("id"),
                        rs.getInt("base_currency_id"),
                        rs.getInt("target_currency_id"),
                        rs.getDouble("rate")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get rate", e);
        }
    }

    public ExchangeRate findByUSD(int baseCurrencyId, int targetCurrencyId){
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?"
            );
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);


            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ExchangeRate(rs.getInt("id"),
                        rs.getInt("base_currency_id"),
                        rs.getInt("target_currency_id"),
                        rs.getDouble("rate")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get rate", e);
        }

    }

    public void changeRate(int baseCurrencyId, int targetCurrencyId, int rate){

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?"
            );
            stmt.setInt(1, rate);
            stmt.setInt(2, baseCurrencyId);
            stmt.setInt(3, targetCurrencyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update rate", e);
        }
    }







}
