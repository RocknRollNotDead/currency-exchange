package ru.codeportfolio.services;

import ru.codeportfolio.db.ExchangeRatesDao;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.mad.ExchangeRate;

import java.sql.Connection;
import java.util.List;

public class RateService {
    private final ExchangeRatesDao exchangeRatesDao;
    private final CurrencyService currencyService;
    public RateService(Connection conn) {
        exchangeRatesDao = new ExchangeRatesDao(conn);

        currencyService = new CurrencyService(conn);
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRatesDao.getAllExchangeRates();
    }

    public void addRate(int baseCurrencyId, int targetCurrencyId, double rate) {
        exchangeRatesDao.addExchangeRate(baseCurrencyId, targetCurrencyId, rate);
    }

    public void deleteRate(String baseCurrencyCode, String targetCurrencyCode){

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        exchangeRatesDao.deleteRate(baseCurrencyId, targetCurrencyId);
    }

    public double getRate (String baseCurrencyCode, String targetCurrencyCode){
        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        double rate = 0;
        ExchangeRate exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId);
        // определить есть ли курс
        if (exchangeRate == null){
            exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, baseCurrencyId);
        }
        if (exchangeRate == null) {
            int USDid = currencyService.getIdFromCode("USD");
            double USDRateBase = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, USDid).getRate();
            double USDRateTarget = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, USDid).getRate();
            rate = USDRateBase / USDRateTarget;
        } else {
            rate = exchangeRate.getRate();
        }

        return rate;
    }

    public void changeRate(String baseCurrencyCode, String targetCurrencyCode, int rate){
        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        exchangeRatesDao.changeRate(baseCurrencyId, targetCurrencyId, rate);
    }
}
