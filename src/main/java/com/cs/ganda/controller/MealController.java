package com.cs.ganda.controller;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Meal;
import com.cs.ganda.dto.SearchParamsDTO;
import com.cs.ganda.service.MealService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/search")
    public @ResponseBody
    List<Meal> search(
            @RequestBody SearchParamsDTO searchParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return this.service.search(searchParams, page, size);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "activate")
    public void activate(@RequestBody ActivationData activationData) {
        this.service.activate(activationData);
    }

}
