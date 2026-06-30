package ru.codeportfolio.db;

import ru.codeportfolio.models.Currency;

import java.util.List;

public interface CurrenciesDaoInterface {
    List<Currency> getAllCurrencies();

    int addCurrency(String code, String fullName, String sign);

    Currency findByCode(String code);

    int updateCurrency(String code, String fullName, String sign);

    int deleteCurrency(String code);
}
