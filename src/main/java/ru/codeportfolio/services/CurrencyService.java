package ru.codeportfolio.services;

import ru.codeportfolio.db.CurrenciesDao;
import ru.codeportfolio.exceptions.AlreadyExistException;
import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.exceptions.NotFoundException;
import ru.codeportfolio.exceptions.ValidationException;
import ru.codeportfolio.mad.Currency;

import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern;

// добавить
// удалить
// посмотреть одну по коду
// посмотреть все

// пока оставим те исключения которые есть, потом когда буду работать с сервлетами поменяю если че в зависимости от потребностей

public class CurrencyService {

    private static final Pattern ADMISSION_CODE = Pattern.compile("^[A-Z0-9]{3}$");
    private final CurrenciesDao currenciesDao;

    public CurrencyService(Connection conn) {
        currenciesDao = new CurrenciesDao(conn);
    }

    public List<Currency> getAllCurrencies() {
        return currenciesDao.getAllCurrencies();
    }

    public Currency getCurrency(String code){

        checkCodeOnEmpty(code);

        code = code.toUpperCase();

        Currency result = currenciesDao.findByCode(code);

        if (result == null) {
            System.out.println(code);
            throw new NotFoundException("currency is null");
        }

        return result;

    }



    public Currency addCurrency(String code, String fullName, String sign) {

        checkValuesOnEmpty(code, fullName, sign);

        code = code.toUpperCase();

        validateValues(code, fullName, sign);

        int result;

        try {
            result = currenciesDao.addCurrency(code, fullName, sign);

        } catch (DataAccessException e){
            System.out.println(code + " " + fullName + sign);
            throw new AlreadyExistException("fail add", e);
        }


        if (result == 0){
            throw new DataAccessException("failed add");
        }

        return currenciesDao.findByCode(code);
    }

    public void updateCurrency(String code, String fullName, String sign) {
        checkValuesOnEmpty(code, fullName, sign);

        code = code.toUpperCase();

        validateValues(code, fullName, sign);

        int result = currenciesDao.updateCurrency(code, fullName, sign);

        if (result == 0){
            throw new NotFoundException("currency not found");
        }

    }

    public void deleteCurrency(String code) {
        checkCodeOnEmpty(code);

        code = code.toUpperCase();

        validateCode(code);

        int result = currenciesDao.deleteCurrency(code);

        if (result == 0){
            throw new NotFoundException("Not found");
        }

    }

    public Currency getCurrencyFromId(int id){
        Currency currency = currenciesDao.findById(id);
        if (currency == null){
            throw new NotFoundException("currency not found");
        }
        return currency;
    }

     protected int getIdFromCode(String code){
         checkCodeOnEmpty(code);
         code = code.toUpperCase();

         Currency currency = currenciesDao.findByCode(code);
         if (currency == null){
             throw new NotFoundException("currency not found");
         }
         return currency.getId();
    }


    private void checkValuesOnEmpty(String code, String fullName, String sign){
        checkCodeOnEmpty(code);
        checkNameOnEmpty(fullName);
        checkSignOnEmpty(sign);
    }

    private void validateValues(String code, String fullName, String sign){
        validateCode(code);
        validateName(fullName);
        validateSign(sign);
    }


    private void checkCodeOnEmpty(String code){
        if (code == null || code.isBlank()){
            throw new ValidationException("code is null or empty");
        }
    }
    private void checkNameOnEmpty(String fullName){
        if (fullName == null || fullName.isBlank()){
            throw new ValidationException("full name is null or empty");
        }
    }
    private void checkSignOnEmpty(String sign) {
        if (sign == null || sign.isBlank()){
            throw new ValidationException("sign is null or empty");
        }
    }

    private void validateCode(String code){
        if (!ADMISSION_CODE.matcher(code).matches()){       //  code.matches("^[A-Z0-9]{2,4}$")
            throw new ValidationException("code must be latin and not many to 4 symbols");
        }
    }
    private void validateName(String fullName){
        if (fullName.length() > 40){
            throw new ValidationException("full name is many 40");
        }
    }
    private void validateSign(String sign){
        if (sign.length() > 2){
            throw new ValidationException("sign is many 2");
        }
    }
}
