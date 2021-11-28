package com.cs.ganda.service;

import java.util.List;

public interface CRUDService<T, ID> {
    List<T> search();

    T create(T t);

    T read(ID id);

    void delete(ID id);

    void update(T t, ID id);
}
