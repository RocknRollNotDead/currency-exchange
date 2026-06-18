package ru.codeportfolio.services;

import ru.codeportfolio.db.CurrenciesDao;
import ru.codeportfolio.exceptions.AlreadyExistException;
import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.exceptions.NotFoundException;
import ru.codeportfolio.exceptions.ValidationException;
import ru.codeportfolio.mad.Currency;

import java.sql.Connection;
import java.util.List;

// добавить
// удалить
// посмотреть одну по коду
// посмотреть все
//

public class CurrencyService {

    private final CurrenciesDao currenciesDao;

    public CurrencyService(Connection conn) {
        currenciesDao = new CurrenciesDao(conn);
    }

    public List<Currency> getAllCurrencies() {
        return currenciesDao.getAllCurrencies();
    }

    public Currency getCurrency(String code){

        if (code == null || code.isBlank()){
            throw new ValidationException("code is null or empty");
        }
        if (!isCurrencyExist(code)){
            throw new NotFoundException("Not found");
        }

        Currency result = currenciesDao.findByCode(code);

        if (result == null) {
            throw new NotFoundException("currency is null");
        }

        return result;

    }

    public void checkValues(String code, String fullName, String sign){
        // проверить код - должно быть 1. 3 символа, 2 - не должно быть то же что есть, 3. должны быть только заглавные английские буквы

        // проверить полное имя - не должно быть уже в бд, только русские, латинские буквы и цифры и пробел
        // знак - должно быть 2 символа(не больше), цифры и пробел запрещены
    }

    public void addCurrency(String code, String fullName, String sign) {
        if (code == null || code.isBlank()){
            throw new ValidationException("code is null or empty");
        }
        if (isCurrencyExist(code)){
            throw new AlreadyExistException("Failed to add");
        }
        if (isSignExist(sign)){
            throw new AlreadyExistException("Failed to add. Sign is exist");
        }

        int result = currenciesDao.addCurrency(code, fullName, sign);

        if (result == 0){
            throw new DataAccessException("failed add");
        }

        // что то не удалось найти нужную валюту, операция сорвалась

    }

    public void updateCurrency(String code, String fullName, String sign) {


        if (!isCurrencyExist(code)){
            throw new NotFoundException("Not found");
        }

        if (isSignExist(sign)){
            throw new AlreadyExistException("Failed to update. Sign is exist");
        }

        int result = currenciesDao.updateCurrency(code, fullName, sign);
        if (result == 0){
            throw new NotFoundException("currency not found");
        }
    }

    public void deleteCurrency(String code) {
        if (code == null || code.isBlank()){
            throw new ValidationException("code is null or empty");
        }
        if (!isCurrencyExist(code)){
            throw new NotFoundException("Not found");
        }

        int result = currenciesDao.deleteCurrency(code);

        if (result == 0){
            throw new DataAccessException("Fail to delete");
        }

    }

     protected int getIdFromCode(String code){
         if (code == null || code.isBlank()){
             throw new ValidationException("code is null or empty!");
         }
         Currency currency = currenciesDao.findByCode(code);
         if (currency == null){
             throw new NotFoundException("currency not found");
         }
         return currency.getId();
    }

    private boolean isCurrencyExist(String code){
        return currenciesDao.findByCode(code) != null;
//        return getCurrency(code) != null;
    }

    private boolean isSignExist(String sign){
        return currenciesDao.findBySign(sign) != null;
    }








}
