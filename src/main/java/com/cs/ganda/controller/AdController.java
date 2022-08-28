package com.cs.ganda.controller;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Ad;
import com.cs.ganda.dto.SearchParamsDTO;
import com.cs.ganda.service.AdService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/ad", produces = APPLICATION_JSON_VALUE)
public class AdController extends ApplicationController<Ad, String> {

    private final AdService service;

    public AdController(AdService service) {
        super(service);
        this.service = service;
    }

    @PostMapping(value = "/search")
    public @ResponseBody
    Stream<Ad> search(
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
