package ru.codeportfolio.db;

import ru.codeportfolio.models.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRatesDaoInterface {
    List<ExchangeRate> getAllExchangeRates();

    int addExchangeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate);

    int deleteRate(int baseCurrencyId, int targetCurrencyId);

    ExchangeRate findByBaseAndTargetId(int baseCurrencyId, int targetCurrencyId);

    int changeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate);
}
