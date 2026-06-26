package ru.codeportfolio.db;

import ru.codeportfolio.exceptions.AlreadyExistException;
import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.mad.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDao {

    private final Connection conn;
    private final CurrenciesDao currenciesDao;

    public ExchangeRatesDao(Connection conn) {
        this.conn = conn;
        currenciesDao = new CurrenciesDao(conn);

    }

    public List<ExchangeRate> getAllExchangeRates() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM exchange_rates")){
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExchangeRate exchangeRate = new ExchangeRate(rs.getInt("id"),
                        currenciesDao.findById(rs.getInt("base_currency_id")),
                        currenciesDao.findById(rs.getInt("target_currency_id")),
                        rs.getBigDecimal("rate") );
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch rates", e);
        }
    }

    public int addExchangeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate) VALUES (?, ?, ?);"
        )){
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            stmt.setBigDecimal(3, rate);

            return stmt.executeUpdate();
        } catch (SQLException e) {
            if (!isCurrencyAlreadyExist(e)){
                throw new DataAccessException("Failed to add rate", e);
            }
            throw new AlreadyExistException("Failed to add rate", e);
        }
    }

    public int deleteRate(int id){ // DAO не должен знать, что делает Service. DAO должен только давать методы для
        try (PreparedStatement stmt = conn.prepareStatement( // CRUD. Он не должен знать, используется там delete или нет.
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

    public ExchangeRate findByBaseAndTargetId(int baseCurrencyId, int targetCurrencyId){

        // потренировался писать такие запросы, но пока что это ни в getAll, ни тем более здесь нафиг не надо

        String sql = """
        SELECT er.id, bc.id as bc_id, bc.code as bc_code, bc.full_name as bc_name, bc.sign as bc_sign,
                tc.id AS tc_id, tc.code as tc_code, tc.full_name as tc_name, tc.sign as tc_sign, er.rate
        FROM exchange_rates er
        JOIN currencies as bc ON bc.id = er.base_currency_id
        JOIN currencies as tc ON tc.id = er.target_currency_id
        WHERE base_currency_id = ? AND target_currency_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Currency baseCurrency = new Currency(
                            rs.getInt("bc_id"),
                            rs.getString("bc_code"),
                            rs.getString("bc_name"),
                            rs.getString("bc_sign")
                    );

                    Currency targetCurrency = new Currency(
                            rs.getInt("tc_id"),
                            rs.getString("tc_code"),
                            rs.getString("tc_name"),
                            rs.getString("tc_sign")
                    );

                    return new ExchangeRate(
                            rs.getInt("id"),
                            baseCurrency,
                            targetCurrency,
                            rs.getBigDecimal("rate")
                    );
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get rate", e);
        }

    }


    public int changeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate){

        try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?"
            )){
            stmt.setBigDecimal(1, rate);
            stmt.setInt(2, baseCurrencyId);
            stmt.setInt(3, targetCurrencyId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update rate", e);
        }
    }

    private boolean isCurrencyAlreadyExist(SQLException e) {
        return e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed");
    }





}
