package org.exchanger.mapper;

public interface RequestMapper<FROM, TO> {

    TO toEntity(FROM source);
}
