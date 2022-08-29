package com.cs.ganda.controller;

import com.cs.ganda.document.Address;
import com.cs.ganda.service.AdService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@CrossOrigin
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "v1/city", produces = APPLICATION_JSON_VALUE)
public class CityController {
    AdService adService;
    @GetMapping
    public Stream<Address> search(@RequestParam(defaultValue = "12") int size)  {
      return this.adService.findAdress(size);
    }

}
