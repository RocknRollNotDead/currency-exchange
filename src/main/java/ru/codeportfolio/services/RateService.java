package ru.codeportfolio.services;

import ru.codeportfolio.db.ExchangeRatesDao;
import ru.codeportfolio.exceptions.*;

import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.mad.Exchange;
import ru.codeportfolio.mad.ExchangeRate;

import java.sql.Connection;
import java.util.List;

public class RateService {
    private static final String MAIN_CURRENCY_CODE = "USD";

    private final ExchangeRatesDao exchangeRatesDao;
    private final CurrencyService currencyService;
    public RateService(Connection conn) {
        exchangeRatesDao = new ExchangeRatesDao(conn);

        currencyService = new CurrencyService(conn);
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRatesDao.getAllExchangeRates();
    }


    public ExchangeRate addRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        checkTargetValuesOnCorrectRequest(baseCurrencyCode, targetCurrencyCode);
        checkValueOnEmpty(rate);
        rate = routingRateToSixSymbolsAfterDot(rate);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);   // тут выпадает NotFoundEx
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);
        int result;

        result = exchangeRatesDao.addExchangeRate(baseCurrencyId, targetCurrencyId, rate);

        if (result == 0){
            throw new DataAccessException("Failed add");
        }

        return exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId);

    }

    public void deleteRate(String baseCurrencyCode, String targetCurrencyCode){

        checkTargetValuesOnCorrectRequest(baseCurrencyCode, targetCurrencyCode);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);   // тут выпадает NotFoundEx
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        int result = exchangeRatesDao.deleteRate(baseCurrencyId, targetCurrencyId);
        if (result == 0){
            throw new NotFoundException("Not found");
        }

    }

    public ExchangeRate getRate (String baseCurrencyCode, String targetCurrencyCode){

        checkTargetValuesOnCorrectRequest(baseCurrencyCode, targetCurrencyCode);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode); // тут упадёт exception
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId);

        if (exchangeRate == null){
            throw new NotFoundException("Not found");
        }

        return exchangeRate;
    }

    public ExchangeRate changeRate(String baseCurrencyCode, String targetCurrencyCode, double rate){
        checkTargetValuesOnCorrectRequest(baseCurrencyCode, targetCurrencyCode);
        checkValueOnEmpty(rate);
        rate = routingRateToSixSymbolsAfterDot(rate);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        int result = exchangeRatesDao.changeRate(baseCurrencyId, targetCurrencyId, rate);

        if (result == 0){
            throw new NotFoundException("Not found");
        }

        ExchangeRate exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId);
        if (exchangeRate != null){
            return exchangeRate;
        } else {
            throw new DataAccessException("Not found");
        }

    }


    public Exchange calculateRate (String baseCurrencyCode, String targetCurrencyCode, double amount){

        checkTargetValuesOnCorrectRequest(baseCurrencyCode, targetCurrencyCode);
        checkValueOnEmpty(amount);

        Currency baseCurrency = currencyService.getCurrency(baseCurrencyCode);
        Currency targetCurrency = currencyService.getCurrency(targetCurrencyCode); // тут упадёт exception

        int baseCurrencyId = baseCurrency.getId();
        int targetCurrencyId = targetCurrency.getId();

        double rate;
        double USDRateBase;
        double USDRateTarget;

        // vvv Логика расчёта курса, AB, BA, USD-A - USD-B vvv

        ExchangeRate exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId);

        if (exchangeRate != null){
            rate = exchangeRate.getRate();
        } else {
            exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, baseCurrencyId);

            if (exchangeRate != null) {
                rate = 1 / exchangeRate.getRate();
            } else {
                int mainCurrency = currencyService.getIdFromCode(MAIN_CURRENCY_CODE);

                try {
                    USDRateBase = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, mainCurrency).getRate();
                } catch (DataAccessException e) {
                    try {
                        USDRateBase = 1 / exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(mainCurrency, baseCurrencyId).getRate();
                    } catch (DataAccessException ee){
                        throw new NotFoundException("Rate Base-USD not found!");
                    }
                }

                try {
                USDRateTarget = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, mainCurrency).getRate();// упадёт датаакцесс
                } catch (DataAccessException e) {
                    try {
                        USDRateTarget = 1 / exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, mainCurrency).getRate();// упадёт датаакцесс
                    } catch (DataAccessException ee){
                        throw new NotFoundException("Rate Target-USD not found!");
                    }
                }
                System.out.println(USDRateBase + " " + USDRateTarget);
                rate = USDRateBase / USDRateTarget;
            }
        }

        if (rate == 0){
            throw new NotFoundException("Rate not found");
        }

        double result = amount * rate;

        result = routingRateToSixSymbolsAfterDot(result);

        return new Exchange(baseCurrency, targetCurrency, rate, amount, result);


    }




    private void checkCodesForEquals(String baseCurrencyCode, String targetCurrencyCode){
        if(baseCurrencyCode.equals(targetCurrencyCode)){
            throw new SelfRatingException("Self rating, what else is there to write?");
        }
    }

    private void checkTargetValuesOnCorrectRequest(String baseCurrencyCode, String targetCurrencyCode){
        checkCodeOnEmpty(baseCurrencyCode);
        checkCodeOnEmpty(targetCurrencyCode);
        checkCodesForEquals(baseCurrencyCode, targetCurrencyCode);
    }
    private void checkCodeOnEmpty(String code){
        if (code == null || code.isBlank()){
            throw new ValidationException("Code is null or empty");
        }
    }
    private void checkValueOnEmpty(double value){
        if(value == 0){
            throw new ValidationException("Value = 0. Value must be not 0");
        }
    }

    private double routingRateToSixSymbolsAfterDot(double rate){
        rate = Math.round(rate * 1_000_000)/1_000_000.0;
        return rate;
    }

}
