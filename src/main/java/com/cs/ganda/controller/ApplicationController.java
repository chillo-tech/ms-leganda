package com.cs.ganda.controller;

import com.cs.ganda.service.CRUDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;

@Slf4j
@CrossOrigin
public abstract class ApplicationController<T, ID extends Serializable> {

    private final CRUDService<T, ID> service;

    public ApplicationController(CRUDService<T, ID> service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public T save(@Valid @RequestBody T json) {
        return this.service.create(json);
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody
    T get(@PathVariable ID id) {
        return this.service.read(id);
    }

    @PutMapping(value = "/{id}")
    public @ResponseBody
    void update(@PathVariable ID id, @RequestBody T e) {
        this.service.update(e, id);
    }


    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable ID id) {
        this.service.delete(id);
    }
}
