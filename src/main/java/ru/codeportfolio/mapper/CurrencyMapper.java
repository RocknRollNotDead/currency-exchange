package ru.codeportfolio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.codeportfolio.DTO.CurrencyDto;
import ru.codeportfolio.mad.Currency;

import java.util.List;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    CurrencyDto toDto(Currency currency);

    List<CurrencyDto> toDtoList(List<Currency> currencies);

    Currency toModel(CurrencyDto dto);
}
