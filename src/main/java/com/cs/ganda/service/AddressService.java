package com.cs.ganda.service;

import com.cs.ganda.document.Address;
import com.cs.ganda.dto.SearchParamsDTO;

import java.util.List;
import java.util.Set;

public interface AddressService extends CRUDService<Address, String> {
    Set<Address> search(String query, boolean autocomplete);
    
    List<Address> search(SearchParamsDTO searchParams);
}

