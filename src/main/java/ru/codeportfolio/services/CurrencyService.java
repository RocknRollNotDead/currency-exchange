package ru.codeportfolio.services;

import ru.codeportfolio.db.CurrenciesDao;
import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.exceptions.NotFoundException;
import ru.codeportfolio.exceptions.ValidationException;
import ru.codeportfolio.mad.Currency;

import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern;

public class CurrencyService {


    private static final Pattern ADMISSION_CODE = Pattern.compile("^[A-Z0-9]{3}$");
    private static final int MAX_NAME_LENGTH = 45;
    private static final int SIGN_LENGTH = 2;

    private final CurrenciesDao currenciesDao;

    public CurrencyService(Connection conn) {
        currenciesDao = new CurrenciesDao(conn);
    }

    public List<Currency> getAllCurrencies() {
        return currenciesDao.getAllCurrencies();
    }

    public Currency getCurrency(String code){

        code = normalizeCode(code);

        Currency result = currenciesDao.findByCode(code);

        if (result == null) {
            throw new NotFoundException("Currency is not found");
        }

        return result;
    }

    public Currency addCurrency(String code, String fullName, String sign) {

        checkValuesOnEmpty(code, fullName, sign);
        code = code.toUpperCase();

        validateValues(code, fullName, sign);

        int result;

        result = currenciesDao.addCurrency(code, fullName, sign);

        if (result == 0){
            throw new DataAccessException("Failed add");
        }

        return currenciesDao.findByCode(code);
    }

    public void updateCurrency(String code, String fullName, String sign) {
        checkValuesOnEmpty(code, fullName, sign);

        code = code.toUpperCase();

        validateValues(code, fullName, sign);

        int result = currenciesDao.updateCurrency(code, fullName, sign);

        if (result == 0){
            throw new NotFoundException("Currency not found");
        }

    }

    public void deleteCurrency(String code) {
        code = normalizeCode(code);

        validateCode(code);

        int result = currenciesDao.deleteCurrency(code);

        if (result == 0){
            throw new NotFoundException("Not found");
        }

    }

    public Currency getCurrencyById(int id){
        Currency currency = currenciesDao.findById(id);
        if (currency == null){
            throw new NotFoundException("Currency not found");
        }
        return currency;
    }

     protected int getIdFromCode(String code){
         code = normalizeCode(code);

         Currency currency = currenciesDao.findByCode(code);
         if (currency == null){
             throw new NotFoundException("Currency not found " + code);
         }
         return currency.getId();
    }



    private String normalizeCode(String code){
        checkStringOnEmptyAndThrowExeption(code, "Code");
        return code.toUpperCase();
    }

    private void checkValuesOnEmpty(String code, String fullName, String sign){
        checkStringOnEmptyAndThrowExeption(code, "Code");
        checkStringOnEmptyAndThrowExeption(fullName, "Full name");
        checkStringOnEmptyAndThrowExeption(sign, "Sign");
    }

    private void validateValues(String code, String fullName, String sign){
        validateCode(code);
        validateName(fullName);
        validateSign(sign);
    }


    private void checkCodeOnEmpty(String code){
        checkStringOnEmptyAndThrowExeption(code, "Code");
    }


    private void checkStringOnEmptyAndThrowExeption(String s, String name){
        if (s == null || s.isBlank()){
            throw new ValidationException(name + " is null or empty");
        }
    }

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
