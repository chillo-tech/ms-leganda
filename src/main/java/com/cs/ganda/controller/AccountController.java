package com.cs.ganda.controller;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.AuthenticationData;
import com.cs.ganda.document.Profile;
import com.cs.ganda.dto.AuthenticationRequest;
import com.cs.ganda.dto.PasswordData;
import com.cs.ganda.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    public static final String MISSING_FIELD = "Merci de sasir le %s";
    public static final String CREDENTIALS_INVALID = "Vos identifiants sont invalide ou votre compte n'est pas actif";
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    @Value("${jwt.accessToken}")
    private String accessToken;
    @Value("${jwt.refreshToken}")
    private String refreshToken;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "phone-activation-code")
    public void phoneActivationCode(@RequestBody ActivationData activationData) {
        this.accountService.phoneActivationCode(activationData);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "activate-phone")
    public @ResponseBody
    AuthenticationData activatePhone(@RequestBody ActivationData activationData, HttpServletResponse response) {
        AuthenticationData authenticationData = this.accountService.activatePhone(activationData);
        response.addCookie(getCookie(accessToken, authenticationData.getAccessToken()));
        response.addCookie(getCookie(refreshToken, authenticationData.getRefreshToken()));
        return authenticationData;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "update-user-profile")
    public @ResponseBody
    AuthenticationData updateProfile(@RequestBody Profile profile, HttpServletResponse response) {
        AuthenticationData authenticationData = this.accountService.updateProfile(profile);
        response.addCookie(getCookie(accessToken, authenticationData.getAccessToken()));
        response.addCookie(getCookie(refreshToken, authenticationData.getRefreshToken()));
        return authenticationData;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "refresh-token")
    public @ResponseBody
    AuthenticationData refreshToken(@RequestBody Map<String, String> data, HttpServletResponse response) {
        AuthenticationData authenticationData = this.accountService.refreshToken(data);
        response.addCookie(getCookie(accessToken, authenticationData.getAccessToken()));
        response.addCookie(getCookie(refreshToken, authenticationData.getRefreshToken()));
        return authenticationData;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(path = "reset-password-link")
    public void resetPasswordLink(@RequestParam String email) {
        this.accountService.resetPasswordLink(email);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "activate")
    public void activate(@RequestBody ActivationData activationData) {
        this.accountService.activate(activationData);
    }


    @PostMapping(path = "signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    AuthenticationData connexion(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        authenticate(authenticationRequest.getPhoneIndex() + "_" + authenticationRequest.getPhone(), authenticationRequest.getPassword());
        AuthenticationData authenticationData = this.accountService.login(authenticationRequest);
        response.addCookie(getCookie(accessToken, authenticationData.getAccessToken()));
        response.addCookie(getCookie(refreshToken, authenticationData.getRefreshToken()));
        return authenticationData;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "add-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void inscription(@RequestBody @Valid Profile profile) {
        this.accountService.register(profile);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "new-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void inscription(@RequestBody @Valid PasswordData passwordData) {
        this.accountService.resetPassword(passwordData);
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(password, String.format(MISSING_FIELD, "mode de passe"));
        Objects.requireNonNull(username, String.format(MISSING_FIELD, "téléphone"));
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new IllegalArgumentException(CREDENTIALS_INVALID);
        }
    }

    private Cookie getCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

}
