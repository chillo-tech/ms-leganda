package com.cs.ganda.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mapboxClient", url = "${providers.mapbox.domain}")
public interface MapBoxClient {
    @GetMapping(value = "${providers.mapbox.places}/{query}.json")
    String search(
            @PathVariable("query") String query,
            @RequestParam(defaultValue = "${providers.mapbox.access_token}") String access_token
    );
}
