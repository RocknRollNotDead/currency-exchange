package ru.codeportfolio.db;

import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.mad.ExchangeRate;
import ru.codeportfolio.services.CurrencyService;

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
    private CurrencyService currencyService;
    public ExchangeRatesDao(Connection conn) {
        this.conn = conn;
        currencyService = new CurrencyService(conn);
    }

    public List<ExchangeRate> getAllExchangeRates() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM exchange_rates")){
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExchangeRate exchangeRate = new ExchangeRate(rs.getInt("id"),
                        currencyService.getCurrencyFromId(rs.getInt("base_currency_id")),
                        currencyService.getCurrencyFromId(rs.getInt("target_currency_id")),
                        rs.getDouble("rate") );
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch rates", e);
        }
    }

    public int addExchangeRate(int baseCurrencyId, int targetCurrencyId, double rate) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate) VALUES (?, ?, ?);"
        )){
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            stmt.setDouble(3, rate);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add rate", e);
        }
    }

    public int deleteRate(int id){
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM exchange_rates WHERE id = ?;"
        )){

            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete rate", e);
        }
    }

    public int deleteRate(int baseCurrencyId, int targetCurrencyId){
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?;"
        )){

            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete rate", e);
        }
    }


    public ExchangeRate findByBaseCurrencyIdAndTargetCurrencyId(int baseCurrencyId, int targetCurrencyId){

        try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?"
            )){
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ExchangeRate(rs.getInt("id"),
                        currencyService.getCurrencyFromId(rs.getInt("base_currency_id")),
                        currencyService.getCurrencyFromId(rs.getInt("target_currency_id")),
                        rs.getDouble("rate")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get rate", e);
        }
    }

    public ExchangeRate findByUSD(int baseCurrencyId, int targetCurrencyId){
        try
            (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?"
            )){
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);


            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ExchangeRate(rs.getInt("id"),
                        currencyService.getCurrencyFromId(rs.getInt("base_currency_id")),
                        currencyService.getCurrencyFromId(rs.getInt("target_currency_id")),
                        rs.getDouble("rate")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get rate", e);
        }

    }

    public int changeRate(int baseCurrencyId, int targetCurrencyId, double rate){

        try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?"
            )){
            stmt.setDouble(1, rate);
            stmt.setInt(2, baseCurrencyId);
            stmt.setInt(3, targetCurrencyId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update rate", e);
        }
    }







}
