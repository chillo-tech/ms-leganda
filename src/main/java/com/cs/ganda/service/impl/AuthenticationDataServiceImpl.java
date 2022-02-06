package com.cs.ganda.service.impl;

import com.cs.ganda.document.AuthenticationData;
import com.cs.ganda.repository.AuthenticationDataRepository;
import com.cs.ganda.service.AuthenticationDataService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.cs.ganda.service.impl.ProfileServiceImpl.USER_NOT_FOUND;

@Service
public class AuthenticationDataServiceImpl extends CRUDServiceImpl<AuthenticationData, String> implements AuthenticationDataService {

    private final AuthenticationDataRepository authenticationDataRepository;

    public AuthenticationDataServiceImpl(AuthenticationDataRepository authenticationDataRepository) {
        super(authenticationDataRepository);
        this.authenticationDataRepository = authenticationDataRepository;
    }

    @Override
    public AuthenticationData findByAccessToken(String token) {
        return this.authenticationDataRepository.findByAccessToken(token).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, "identifiant transmis")));
    }
}
