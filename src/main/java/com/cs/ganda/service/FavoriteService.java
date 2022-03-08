package com.cs.ganda.service;

import com.cs.ganda.document.Ad;
import com.cs.ganda.enums.Action;

import java.util.List;
import java.util.Set;

public interface FavoriteService {
    void toggle(String addId, Action action);

    Set<String> getFavoritesProvidersId();

    List<Ad> getFavoritesAds();
}
