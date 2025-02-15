package org.myapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Currency {

    private int id;
    private String code;
    private String fullName;
    private String sign;

    public Currency() {
    }

    public Currency(@JsonProperty("id") int id,
                    @JsonProperty("code") String code,
                    @JsonProperty("full_name") String fullName,
                    @JsonProperty("sign") String sign) {
        this.id = id;
        this.fullName = fullName;
        this.code = code;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
