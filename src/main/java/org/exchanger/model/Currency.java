package org.exchanger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "code", "sign"})
public class Currency {

    private final Integer id;
    private final String fullName;
    private final String code;
    private final String sign;

    public Currency(Integer id, String fullName, String code, String sign) {
        this.id = id;
        this.fullName = fullName;
        this.code = code.toUpperCase();
        this.sign = sign;
    }

    // todo: null?
    public Currency(String fullName, String code, String sign) {
        this(null, fullName, code, sign);
    }

    public Integer getId() {
        return id;
    }

    @JsonProperty("name")
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
