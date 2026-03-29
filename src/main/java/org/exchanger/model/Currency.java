package org.exchanger.model;

public class Currency {

    private Long id;
    private final String fullName;
    private final String code;
    private final String sign;

    public Currency(Long id, String fullName, String code, String sign) {
        this.id = id;
        this.fullName = fullName;
        this.code = code;
        this.sign = sign;
    }

    public Currency(String fullName, String code, String sign) {
        this(null, fullName, code, sign);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
