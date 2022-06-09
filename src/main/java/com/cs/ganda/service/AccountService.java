package com.cs.ganda.service;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Address;
import com.cs.ganda.document.AuthenticationData;
import com.cs.ganda.document.Profile;
import com.cs.ganda.dto.AuthenticationRequest;
import com.cs.ganda.dto.PasswordData;

import java.util.Map;

public interface AccountService {
    void resetPassword(PasswordData passwordData);

    void register(Profile profile);

    AuthenticationData login(AuthenticationRequest authenticationRequest);

    void activate(ActivationData activationData);

    AuthenticationData activatePhone(ActivationData activationData);

    void resetPasswordLink(String email);

    void phoneActivationCode(ActivationData activationData);

    AuthenticationData updateProfile(Profile profile);

    AuthenticationData refreshToken(Map<String, String> data);

    void updateAddress(Address address);
}
