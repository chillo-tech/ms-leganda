package com.cs.ganda.service;


import com.cs.ganda.document.Address;
import com.cs.ganda.document.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ProfileService extends UserDetailsService {
    Profile getAuthenticateUser();
    void register(Profile profile);

    Profile update(Profile profile);

    Profile findByEmail(String email);

    Profile findById(String id);

    Profile findByPhoneAndPhoneIndex(String phone, String phoneIndex);

    void updateAddress(Address address);
}
