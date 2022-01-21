package com.cs.ganda.api;

import com.cs.ganda.dto.SearchParamsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mapboxClient", url = "${providers.mapbox.domain}")
public interface MapBoxClient {
    @GetMapping(value = "${providers.mapbox.places}/{query}.json" +
            "?limit=5" +
            "&access_token=${providers.mapbox.access_token}" +
            "&country=${providers.mapbox.country}"
    )
    String search(
            @PathVariable("query") String query,
            @SpringQueryMap SearchParamsDTO searchParams
    );
}
