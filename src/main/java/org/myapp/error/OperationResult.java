package org.myapp.error;

import org.myapp.dto.CurrencyDto;

public class OperationResult {
    private boolean success;
    private String message;
    private CurrencyDto dto;

    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public OperationResult(boolean success, String message, CurrencyDto dto) {
        this.success = success;
        this.message = message;
        this.dto = dto;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CurrencyDto getDto() {
        return dto;
    }

    public void setDto(CurrencyDto dto) {
        this.dto = dto;
    }
}
