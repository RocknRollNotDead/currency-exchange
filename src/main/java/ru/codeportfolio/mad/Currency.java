package ru.codeportfolio.mad;

import java.util.Objects;

public final class Currency {
    private final int id;
    private final String code;
    private final String name;
    private final String sign;

    public Currency(int id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Currency) obj;
        return this.id == that.id &&
                Objects.equals(this.code, that.code) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, sign);
    }

    @Override
    public String toString() {
        return "Currencies[" +
                "id=" + id + ", " +
                "code=" + code + ", " +
                "name=" + name + ", " +
                "sign=" + sign + ']';
    }

}
