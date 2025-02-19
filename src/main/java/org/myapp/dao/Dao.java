package org.myapp.dao;

import java.util.List;

public interface Dao<T> {

    int save(T currency);
    T findById(int id);
    List<T> findAll();

    T findByCode(String code);

    void update(T currency);
}