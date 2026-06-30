package ru.codeportfolio.db;

import ru.codeportfolio.models.Currency;

import java.util.List;

public interface CurrenciesDaoInterface {
    List<Currency> getAll();

    int add(String code, String fullName, String sign);

    Currency findByCode(String code);

    int update(String code, String fullName, String sign);

    int delete(String code);
}
