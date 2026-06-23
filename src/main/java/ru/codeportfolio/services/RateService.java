package ru.codeportfolio.services;

import ru.codeportfolio.db.ExchangeRatesDao;
import ru.codeportfolio.exceptions.AlreadyExistException;
import ru.codeportfolio.exceptions.DataAccessException;
import ru.codeportfolio.exceptions.NotFoundException;
import ru.codeportfolio.exceptions.ValidationException;
import ru.codeportfolio.mad.CreateRateAnswer;
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


    public void addRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        checkTargetValuesOnEmpty(baseCurrencyCode, targetCurrencyCode);
        checkRateOnEmpty(rate);
        rate = routingRateToSixSymbolsAfterZapyataya(rate);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);   // тут выпадает NotFoundEx
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);
        int result;

        try{
            result = exchangeRatesDao.addExchangeRate(baseCurrencyId, targetCurrencyId, rate);
        } catch (DataAccessException e){
            throw new AlreadyExistException("fail add", e);
        }
        //
        if (result == 0){
            throw new DataAccessException("failed add");
        }
    }

    public void deleteRate(String baseCurrencyCode, String targetCurrencyCode){

        checkTargetValuesOnEmpty(baseCurrencyCode, targetCurrencyCode);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);   // тут выпадает NotFoundEx
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        int result = exchangeRatesDao.deleteRate(baseCurrencyId, targetCurrencyId);
        if (result == 0){
            throw new NotFoundException("Not found");
        }

    }

    public CreateRateAnswer getRate (String baseCurrencyCode, String targetCurrencyCode){

        checkTargetValuesOnEmpty(baseCurrencyCode, targetCurrencyCode);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode); // тут упадёт exception
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        double rate;

        // vvv Логика расчёта курса, AB, BA, USD-A - USD-B vvv

        // определить есть ли курс
        ExchangeRate exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId);
        // если нет - попробовать обратный
        if (exchangeRate == null){
            exchangeRate = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, baseCurrencyId);
        }
        // если и обратного нет, вычисляем по USD
        if (exchangeRate == null) {
            int mainCurrency = currencyService.getIdFromCode(MAIN_CURRENCY_CODE);
            double USDRateBase = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(targetCurrencyId, mainCurrency).getRate();
            double USDRateTarget = exchangeRatesDao.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, mainCurrency).getRate();// упадёт датаакцессэкс
            rate = USDRateBase / USDRateTarget;
        } else { // если есть - берём число из сущности курс, которую мы нашли прямым или обратным путём
            rate = exchangeRate.getRate();
        }

        CreateRateAnswer rateAnswer = new CreateRateAnswer();

        return rate;
    }

    public void changeRate(String baseCurrencyCode, String targetCurrencyCode, double rate){
        checkTargetValuesOnEmpty(baseCurrencyCode, targetCurrencyCode);
        checkRateOnEmpty(rate);
        rate = routingRateToSixSymbolsAfterZapyataya(rate);

        int baseCurrencyId = currencyService.getIdFromCode(baseCurrencyCode);
        int targetCurrencyId = currencyService.getIdFromCode(targetCurrencyCode);

        exchangeRatesDao.changeRate(baseCurrencyId, targetCurrencyId, rate);
    }




    private void checkTargetValuesOnEmpty(String baseCurrencyCode, String targetCurrencyCode){
        checkCodeOnEmpty(baseCurrencyCode);
        checkCodeOnEmpty(targetCurrencyCode);
    }
    private void checkCodeOnEmpty(String code){
        if (code == null || code.isBlank()){
            throw new ValidationException("code is null or empty");
        }
    }
    private void checkRateOnEmpty(double rate){
        if(rate == 0){
            throw new ValidationException("rate = 0");
        }
    }
    private double routingRateToSixSymbolsAfterZapyataya(double rate){
        rate = Math.round(rate * 1_000_000)/1_000_000.0;
        return rate;
    }

}
