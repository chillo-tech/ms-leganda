package com.cs.ganda.service.impl;

import com.cs.ganda.api.MapBoxClient;
import com.cs.ganda.document.Address;
import com.cs.ganda.document.Location;
import com.cs.ganda.dto.SearchParamsDTO;
import com.cs.ganda.repository.AddressRepository;
import com.cs.ganda.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AddressServiceImpl extends CRUDServiceImpl<Address, String> implements AddressService {

    private final MapBoxClient mapBoxClient;

    public AddressServiceImpl(
            AddressRepository addressRepository,
            MapBoxClient mapBoxClient
    ) {
        super(addressRepository);
        this.mapBoxClient = mapBoxClient;
    }

    @Override
    public Stream<Address> search(String query, String mapBoxtypes, String origin, boolean autocomplete) {
        SearchParamsDTO searchParams = new SearchParamsDTO();
        searchParams.setTypes(mapBoxtypes);
        if (origin.isEmpty()) {
            searchParams.setOrigin(origin);
        }
        String response = this.mapBoxClient.search(query.replaceAll(" ", "-"), searchParams);
        JSONObject json = new JSONObject(response);
        JSONArray features = json.getJSONArray("features");

        return StreamSupport
                .stream(features.spliterator(), true)
                .map((item) -> {
                    Address address = new Address();

                    String id = ((JSONObject) item).getString("id");
                    address.setId(id);

                    String street = ((JSONObject) item).getString("place_name");
                    address.setStreet(street);

                    JSONObject location = ((JSONObject) item).getJSONObject("geometry");
                    String type = location.getString("type");

                    JSONArray coordinates = location.getJSONArray("coordinates");
                    double latitude = coordinates.getDouble(0);
                    double longitude = coordinates.getDouble(1);
                    double[] latLong = {latitude, longitude};

                    address.setLocation(new Location(type, latLong));
                    return address;
                });
    }

}
