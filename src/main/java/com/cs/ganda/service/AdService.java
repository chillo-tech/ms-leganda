package com.cs.ganda.service;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Ad;
import com.cs.ganda.dto.SearchParamsDTO;

import java.util.List;

public interface AdService extends CRUDService<Ad, String> {
    void activate(ActivationData activationData);

    List<Ad> search(SearchParamsDTO searchParams, int page, int size);

    List<Ad> findAllByProfileIdIn(List<String> ids);
}
