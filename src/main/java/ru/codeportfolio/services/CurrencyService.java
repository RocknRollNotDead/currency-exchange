package ru.codeportfolio.services;

import ru.codeportfolio.db.CurrenciesDao;
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
        // попробовать получить результат
        // в случае ошибки кинуть throw
        return currenciesDao.getAllCurrencies();
    }

    public Currency getCurrency(String code){

        return currenciesDao.findByCode(code);
    }

    public void checkValues(String code, String fullName, String sign){
        // проверить код - должно быть 1. 3 символа, 2 - не должно быть то же что есть, 3. должны быть только заглавные английские буквы

        // проверить полное имя - не должно быть уже в бд, только русские, латинские буквы и цифры и пробел
        // знак - должно быть 2 символа(не больше), цифры и пробел запрещены
    }

    public void addCurrency(String code, String fullName, String sign) {
        currenciesDao.addCurrency(code, fullName, sign);
    }

    public void updateCurrency(String code, String fullName, String sign) {
        currenciesDao.updateCurrency(code, fullName, sign);
    }

    public void deleteCurrency(String code) {
        currenciesDao.deleteCurrency(code);
    }

    public int getIdFromCode(String code){
        Currency currency = currenciesDao.findByCode(code);
        return currency.getId();
    }








}
