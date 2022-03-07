package com.cs.ganda;

import com.cs.ganda.enums.Action;
import com.cs.ganda.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/favorite", produces = APPLICATION_JSON_VALUE)
public class FavoriteController {

    private final FavoriteService service;

    public FavoriteController(FavoriteService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "toggle")
    public void activate(@RequestBody Map<String, String> params) {
        this.service.toggle(params.get("id"), Action.valueOf(params.get("action")));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Set<String> search() {
        return this.service.search();
    }

}
