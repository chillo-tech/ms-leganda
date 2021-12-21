package com.cs.ganda.controller;

import com.cs.ganda.document.Address;
import com.cs.ganda.service.AddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/address", produces = APPLICATION_JSON_VALUE)
public class AddressController extends ApplicationController<Address, String> {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        super(addressService);
        this.addressService = addressService;
    }

    @GetMapping
    public @ResponseBody
    Set<Address> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "address") String types,
            @RequestParam(defaultValue = "false") boolean autocomplete) {
        return this.addressService.search(query, types, autocomplete);
    }


}
