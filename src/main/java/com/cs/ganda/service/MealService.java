package com.cs.ganda.service;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Meal;
import com.cs.ganda.dto.SearchParamsDTO;

import java.util.List;

public interface MealService extends CRUDService<Meal, String> {
    void activate(ActivationData activationData);

    List<Meal> search(SearchParamsDTO searchParams, int page, int size);
}
