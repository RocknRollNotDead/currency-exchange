package ru.codeportfolio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.codeportfolio.DTO.CurrencyDto;
import ru.codeportfolio.DTO.ExchangeRateDto;
import ru.codeportfolio.mad.Currency;
import ru.codeportfolio.mad.ExchangeRate;

import java.util.List;

@Mapper(uses = CurrencyMapper.class)


public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);
    ExchangeRateDto toDto(ExchangeRate exchangeRate);

    List<ExchangeRateDto> toDtoList(List<ExchangeRate> exchangeRates);

    ExchangeRate toModel(ExchangeRateDto dto);
}
