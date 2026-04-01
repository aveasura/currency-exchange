package org.exchanger.mapper;

public interface ResponseMapper<FROM, TO> {

    TO toDto(FROM source);
}
