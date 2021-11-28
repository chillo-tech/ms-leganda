package com.cs.ganda.controller;

import com.cs.ganda.document.Meal;
import com.cs.ganda.service.MealService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/meal", produces = APPLICATION_JSON_VALUE)
public class MealController extends ApplicationController<Meal, String> {

    private final MealService service;

    public MealController(MealService service) {
        super(service);
        this.service = service;
    }

    @GetMapping
    public @ResponseBody
    List<Meal> search() {
        return this.service.search();
    }

}
