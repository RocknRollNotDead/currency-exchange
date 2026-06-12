package ru.codeportfolio.mad;

import java.util.Objects;

public final class Currency {
    private final int id;
    private final String code;
    private final String fullName;
    private final String sign;

    public Currency(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
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
                Objects.equals(this.fullName, that.fullName) &&
                Objects.equals(this.sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, sign);
    }

    @Override
    public String toString() {
        return "Currencies[" +
                "id=" + id + ", " +
                "code=" + code + ", " +
                "fullName=" + fullName + ", " +
                "sign=" + sign + ']';
    }

}
