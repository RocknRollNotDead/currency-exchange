package ru.codeportfolio.DTO;

import ru.codeportfolio.mad.Currency;

import java.math.BigDecimal;

// Это повторение класса ExchangeRate (это модель). Оно нужно только для соблюдение тз. На работу программы не влияет никак.

public record ExchangeRateDto(int id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ExchangeRateDto) obj;
        return this.id == that.id &&
                this.baseCurrency == that.baseCurrency &&
                this.targetCurrency == that.targetCurrency /*&&
                Double.doubleToLongBits(this.rate) == Double.doubleToLongBits(that.rate)*/;
    }

    @Override
    public String toString() {
        return "ExchangeRates[" +
                "id=" + id + ", " +
                "baseCurrencyId=" + baseCurrency + ", " +
                "targetCurrencyId=" + targetCurrency + ", " +
                "rate=" + rate + ']';
    }

}
