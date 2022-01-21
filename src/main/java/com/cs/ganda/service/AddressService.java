package com.cs.ganda.service;

import com.cs.ganda.document.Address;

import java.util.Set;

public interface AddressService extends CRUDService<Address, String> {
    Set<Address> search(
            String query,
            String type,
            String proximity,
            boolean autocomplete
    );

}

