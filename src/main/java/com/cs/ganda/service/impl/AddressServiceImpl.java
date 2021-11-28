package com.cs.ganda.service.impl;

import com.cs.ganda.api.MapBoxClient;
import com.cs.ganda.document.Address;
import com.cs.ganda.document.Location;
import com.cs.ganda.repository.AddressRepository;
import com.cs.ganda.service.AddressService;
import com.cs.ganda.service.CommonsMethods;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AddressServiceImpl extends CRUDServiceImpl<Address, String> implements AddressService {

    public static final String USER_NOT_FOUND = "Aucun profile ne correspond à %s";
    private final AddressRepository addressRepository;
    private final CommonsMethods commonsMethods;
    private final MapBoxClient mapBoxClient;

    @Value("${providers.mapbox.access_token}")
    private String accessToken;

    public AddressServiceImpl(
            AddressRepository addressRepository,
            MapBoxClient mapBoxClient,
            CommonsMethods commonsMethods
    ) {
        super(addressRepository);
        this.addressRepository = addressRepository;
        this.commonsMethods = commonsMethods;
        this.mapBoxClient = mapBoxClient;
    }


    @Override
    public Set<Address> search(String query, boolean autocomplete) {
        String response = this.mapBoxClient.search(query, accessToken);
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

                    address.setCoordinates(new Location(type, latLong));
                    return address;
                }).collect(Collectors.toSet());
    }
}
