package org.exchanger.mapper;

public interface ResponseMapper<S, T>{

    T toDto(S source);
}
