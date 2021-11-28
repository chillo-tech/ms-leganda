package com.cs.ganda.controller;

import com.cs.ganda.document.Address;
import com.cs.ganda.service.AddressService;
import org.springframework.web.bind.annotation.*;

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
    Set<Address> search(@RequestParam String query, @RequestParam(defaultValue = "false") boolean autocomplete) {
        return this.addressService.search(query, autocomplete);
    }


}
