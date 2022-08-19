package com.cs.ganda.service.impl;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class SecurityService {
    public Principal getAuthencicatedUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
