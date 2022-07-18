package com.cs.ganda.controller;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Category;
import com.cs.ganda.dto.AuthenticationRequest;
import com.cs.ganda.repository.CategoryRepository;
import com.cs.ganda.service.CategoryService;

import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "v1/category", produces = APPLICATION_JSON_VALUE)
public class CategoryController extends ApplicationController<Category, String> {
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private CategoryController accountService;

    public CategoryController(CategoryService categoryService,CategoryRepository categoryRepository) {
        super(categoryService);
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Category> search()  {return categoryService.findAll();}


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "activate"  ,consumes = APPLICATION_JSON_VALUE)
    public void activate(@RequestBody ActivationData activationData) {
        this.accountService.activate(activationData);
    }

    @PostMapping(path = "signin" , consumes = APPLICATION_JSON_VALUE)
    public @ResponseBody
    void connexion(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletResponse response)
    { this.categoryService.signin(authenticationRequest,response); }


}