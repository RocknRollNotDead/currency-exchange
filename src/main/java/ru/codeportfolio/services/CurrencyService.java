package ru.codeportfolio.services;

import ru.codeportfolio.DTO.CurrencyDto;
import ru.codeportfolio.db.CurrenciesDao;
import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.exceptions.NotFoundException;
import ru.codeportfolio.exceptions.ValidationException;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.mapper.CurrencyMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class CurrencyService {


    private static final Pattern ADMISSION_CODE = Pattern.compile("^[A-Z0-9]{3}$");
    private static final int MAX_NAME_LENGTH = 45;
    private static final int SIGN_LENGTH = 2;

    private final DataSource dataSource;

    public CurrencyService(DataSource dataSource) {
        this.dataSource = dataSource;

    }

    public List<CurrencyDto> getAllCurrencies() {


        try (Connection conn = dataSource.getConnection()) {

            CurrenciesDao currenciesDao = new CurrenciesDao(conn);

            return CurrencyMapper.INSTANCE.toDtoList(currenciesDao.getAllCurrencies());

        } catch (SQLException e) {
            throw new DataAccessException("DB error", e);
        }
    }

    public CurrencyDto getCurrency(String code){

        code = normalizeCode(code);
        Currency result;

        try (Connection conn = dataSource.getConnection()) {
            CurrenciesDao currenciesDao = new CurrenciesDao(conn);
            result = currenciesDao.findByCode(code);

            if (result == null) {
                throw new NotFoundException("Currency is not found");
            }

            return CurrencyMapper.INSTANCE.toDto(result);
        } catch (SQLException e) {
            throw new DataAccessException("DB error", e);
        }
    }

    public CurrencyDto addCurrency(String code, String fullName, String sign) {

        checkValuesOnEmpty(code, fullName, sign);
        code = code.toUpperCase();

        validateValues(code, fullName, sign);

        int result;

        try (Connection conn = dataSource.getConnection()) {
            CurrenciesDao currenciesDao = new CurrenciesDao(conn);

            result = currenciesDao.addCurrency(code, fullName, sign);

            if (result == 0){
                throw new DataAccessException("Failed add");
            }

            return CurrencyMapper.INSTANCE.toDto(currenciesDao.findByCode(code));


        } catch (SQLException e) {
            throw new DataAccessException("DB error", e);
        }


    }

    public CurrencyDto updateCurrency(String code, String fullName, String sign) {
        checkValuesOnEmpty(code, fullName, sign);

        code = code.toUpperCase();

        validateValues(code, fullName, sign);

        try (Connection conn = dataSource.getConnection()) {

            CurrenciesDao currenciesDao = new CurrenciesDao(conn);

            int result = currenciesDao.updateCurrency(code, fullName, sign);

            if (result == 0){
                throw new NotFoundException("Currency not found");
            }

            return CurrencyMapper.INSTANCE.toDto(currenciesDao.findByCode(code));

        } catch (SQLException e) {
            throw new DataAccessException("DB error", e);
        }



    }

    public void deleteCurrency(String code) {
        code = normalizeCode(code);

        validateCode(code);

        try (Connection conn = dataSource.getConnection()) {

            CurrenciesDao currenciesDao = new CurrenciesDao(conn);

            int result = currenciesDao.deleteCurrency(code);

            if (result == 0){
                throw new NotFoundException("Not found");
            }

        } catch (SQLException e) {
            throw new DataAccessException("DB error", e);
        }



    }

    public CurrencyDto getCurrencyById(int id){

        try (Connection conn = dataSource.getConnection()) {

            CurrenciesDao currenciesDao = new CurrenciesDao(conn);

            Currency currency = currenciesDao.findById(id);
            if (currency == null){
                throw new NotFoundException("Currency not found");
            }
            return CurrencyMapper.INSTANCE.toDto(currency);

        } catch (SQLException e) {
            throw new DataAccessException("DB error", e);
        }
    }

     protected int getIdFromCode(String code){
         code = normalizeCode(code);

         try (Connection conn = dataSource.getConnection()) {

             CurrenciesDao currenciesDao = new CurrenciesDao(conn);

             Currency currency = currenciesDao.findByCode(code);
             if (currency == null){
                 throw new NotFoundException("Currency not found " + code);
             }
             return currency.id();

         } catch (SQLException e) {
             throw new DataAccessException("DB error", e);
         }


    }




    private String normalizeCode(String code){
        checkStringOnEmptyAndThrowException(code, "Code");
        return code.toUpperCase();
    }

    private void checkValuesOnEmpty(String code, String fullName, String sign){
        checkStringOnEmptyAndThrowException(code, "Code");
        checkStringOnEmptyAndThrowException(fullName, "Full name");
        checkStringOnEmptyAndThrowException(sign, "Sign");
    }

    private void validateValues(String code, String fullName, String sign){
        validateCode(code);
        validateName(fullName);
        validateSign(sign);
    }

    private void checkStringOnEmptyAndThrowException(String s, String name){
        if (s == null || s.isBlank()){
            throw new ValidationException(name + " is null or empty");
        }
    }

    // Принципиально не буду делать Validator классы ради 40 строчек. 1. Это задача сервиса. 2. Будет менее читаемо

    private void validateCode(String code){
        if (!ADMISSION_CODE.matcher(code).matches()){       //  code.matches("^[A-Z0-9]{3}$")
            throw new ValidationException("Code must be latin and length 3 symbols");
        }
    }
    private void validateName(String fullName){
        if (fullName.length() > MAX_NAME_LENGTH){
            throw new ValidationException("Full name is many " + MAX_NAME_LENGTH);
        }
    }
    private void validateSign(String sign){
        if (sign.length() > SIGN_LENGTH){
            throw new ValidationException("Sign is many " + SIGN_LENGTH);
        }
    }
}
