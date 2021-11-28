package com.cs.ganda.service;

import com.cs.ganda.document.Meal;
import com.cs.ganda.dto.MealDTO;

import java.util.Set;

public interface MealService extends CRUDService<Meal, String> {
    Set<MealDTO> findMeals();
}
