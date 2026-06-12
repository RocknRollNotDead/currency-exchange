package ru.codeportfolio.mad;

import java.util.Objects;

public final class ExchangeRate {
    private final int id;
    private final int baseCurrencyId;
    private final int targetCurrencyId;
    private final double rate;

    public ExchangeRate(int id, int baseCurrencyId, int targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
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
                this.baseCurrencyId == that.baseCurrencyId &&
                this.targetCurrencyId == that.targetCurrencyId &&
                Double.doubleToLongBits(this.rate) == Double.doubleToLongBits(that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyId, targetCurrencyId, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRates[" +
                "id=" + id + ", " +
                "baseCurrencyId=" + baseCurrencyId + ", " +
                "targetCurrencyId=" + targetCurrencyId + ", " +
                "rate=" + rate + ']';
    }

}
