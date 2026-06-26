package ru.codeportfolio.mad;

public record Currency(int id, String code, String name, String sign) {

    @Override
    public String toString() {
        return "Currencies[" +
                "id=" + id + ", " +
                "code=" + code + ", " +
                "name=" + name + ", " +
                "sign=" + sign + ']';
    }

}
