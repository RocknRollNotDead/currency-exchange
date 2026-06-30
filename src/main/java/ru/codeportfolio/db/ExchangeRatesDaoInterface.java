package ru.codeportfolio.db;

import ru.codeportfolio.models.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRatesDaoInterface {
    List<ExchangeRate> getAll();

    int add(int baseCurrencyId, int targetCurrencyId, BigDecimal rate);

    int delete(int baseCurrencyId, int targetCurrencyId);

    ExchangeRate findById(int baseCurrencyId, int targetCurrencyId);

    int update(int baseCurrencyId, int targetCurrencyId, BigDecimal rate);
}
