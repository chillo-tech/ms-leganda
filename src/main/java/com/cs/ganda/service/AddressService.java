package com.cs.ganda.service;

import com.cs.ganda.document.Address;

import java.util.stream.Stream;

public interface AddressService extends CRUDService<Address, String> {
    Stream<Address> search(
            String query,
            String type,
            String proximity,
            boolean autocomplete
    );

}

