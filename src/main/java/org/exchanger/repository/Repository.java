package org.exchanger.repository;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
}
