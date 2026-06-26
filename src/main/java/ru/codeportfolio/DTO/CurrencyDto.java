package ru.codeportfolio.DTO;


// Это повторение класса Currency (это модель). Оно нужно только для соблюдение тз. На работу программы не влияет никак.

public record CurrencyDto(int id, String code, String name, String sign) {

    @Override
    public String toString() {
        return "Currencies[" +
                "id=" + id + ", " +
                "code=" + code + ", " +
                "name=" + name + ", " +
                "sign=" + sign + ']';
    }
}
