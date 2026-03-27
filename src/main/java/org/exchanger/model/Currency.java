package org.exchanger.model;

public class Currency {

    private final Long id;
    private final String fullName;
    private final String code;
    private final String sign;

    public Currency(Long id, String fullName, String code, String sign) {
        this.id = id;
        this.fullName = fullName;
        this.code = code;
        this.sign = sign;
    }

    // todo: null?
    public Currency(String fullName, String code, String sign) {
        this(null, fullName, code, sign);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCode() {
        return code;
    }

    public String getSign() {
        return sign;
    }
}
