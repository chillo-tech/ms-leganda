package com.cs.ganda.controller;

import com.cs.ganda.document.Category;
import com.cs.ganda.service.CategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/category", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class CategoryController extends ApplicationController<Category, String> {

    public CategoryController(CategoryService categoryService) {
        super(categoryService);
    }
}
