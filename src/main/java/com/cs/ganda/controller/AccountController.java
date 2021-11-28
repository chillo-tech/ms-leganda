package com.cs.ganda.controller;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Profile;
import com.cs.ganda.service.impl.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "activate")
    public void activate(@RequestBody ActivationData activationData) {
        this.accountService.activate(activationData);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "add-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void inscription(@RequestBody @Valid Profile profile) {
        this.accountService.register(profile);
    }

}
