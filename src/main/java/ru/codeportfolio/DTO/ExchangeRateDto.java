package ru.codeportfolio.DTO;

import ru.codeportfolio.mad.Currency;

import java.math.BigDecimal;
import java.util.Objects;

// Это повторение класса ExchangeRate (это модель). Оно нужно только для соблюдение тз. На работу программы не влияет никак.

public record ExchangeRateDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {


}
