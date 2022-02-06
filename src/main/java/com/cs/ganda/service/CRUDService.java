package com.cs.ganda.service;

import com.cs.ganda.document.Profile;

public interface CRUDService<T, ID> {

    T create(T t);

    T read(ID id);

    void delete(ID id);

    void update(T t, ID id);

    Profile getAuthenticatedProfile();
}
