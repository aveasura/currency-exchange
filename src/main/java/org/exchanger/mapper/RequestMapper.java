package org.exchanger.mapper;

public interface RequestMapper <S, T>{

    T toEntity(S source);
}
