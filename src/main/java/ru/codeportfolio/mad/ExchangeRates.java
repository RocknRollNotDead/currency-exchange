package ru.codeportfolio.mad;

public record ExchangeRates(int id, int baseCurrencyId, int target_currency_id, double rate) {
}
