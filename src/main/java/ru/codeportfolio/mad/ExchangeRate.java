package ru.codeportfolio.mad;

import java.util.Objects;

public final class ExchangeRate {
    private final int id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final double rate;

    public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;

        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ExchangeRate) obj;
        return this.id == that.id &&
                this.baseCurrency == that.baseCurrency &&
                this.targetCurrency == that.targetCurrency &&
                Double.doubleToLongBits(this.rate) == Double.doubleToLongBits(that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrency, targetCurrency, rate);
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
