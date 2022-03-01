package com.cs.ganda.service;

import com.cs.ganda.document.AuthenticationData;

public interface AuthenticationDataService extends CRUDService<AuthenticationData, String> {
    AuthenticationData findByAccessToken(String token);

    AuthenticationData findByRefreshoken(String token);
}
