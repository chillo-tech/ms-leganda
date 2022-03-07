package com.cs.ganda.service;

import com.cs.ganda.enums.Action;

import java.util.Set;

public interface FavoriteService {
    void toggle(String addId, Action action);

    Set<String> search();
}
