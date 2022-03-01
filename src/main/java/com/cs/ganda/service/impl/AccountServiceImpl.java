package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.AuthenticationData;
import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.document.Profile;
import com.cs.ganda.dto.AuthenticationRequest;
import com.cs.ganda.dto.PasswordData;
import com.cs.ganda.enums.UserRole;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.security.JwtTokenUtil;
import com.cs.ganda.service.AccountService;
import com.cs.ganda.service.AuthenticationDataService;
import com.cs.ganda.service.CommonsMethods;
import com.cs.ganda.service.ConfirmationTokenService;
import com.cs.ganda.service.ProfileService;
import com.cs.ganda.validators.UserNameValidator;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.lang.Boolean.TRUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private static final String EMAIL_INVALID = "l'email %s est invalide";
    private static final String PHONE_INVALID = "le téléphone %s est invalide";
    private static final String CODE_INVALID = "le code que vous avez saisi est invalide";
    private static final String MISSING_FIELD = "Merci de sasir le %s";
    private static final String ACCOUNT_EXISTS = "Un compte avec %s %s existe. Avez vous oublié votre mot de passe ?";
    private static final String ACCOUNT_NOT_EXISTS = "Aucun compte ne correspond à %s %s.";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserNameValidator userNameValidator;
    private final ProfileService profileService;
    private final ProfileRepository profilRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ConfirmationTokenService confirmationTokenService;
    private final AuthenticationDataService authenticationDataService;
    private final CommonsMethods commonsMethods;
    private final MongoTemplate mongoTemplate;

    @Value("${jwt.accessToken}")
    private String accessToken;

    @Value("${jwt.refreshToken}")
    private String refreshToken;

    @Value("${account.activation.send}")
    private Boolean sendAccountActivationCode;

    @Value("${account.activation.code}")
    private String accountActivationCode;

    public void resetPassword(PasswordData passwordData) {
        Objects.requireNonNull(passwordData.getPassword(), String.format(MISSING_FIELD, "mot de passe"));
        Objects.requireNonNull(passwordData.getConfirmation(), String.format(MISSING_FIELD, "confirmation"));

        ConfirmationToken confirmationToken = this.confirmationTokenService.getConfirmationToken(passwordData.getToken());
        final Profile profile = confirmationToken.getProfile();
        if (!passwordData.getConfirmation().equals(passwordData.getPassword())) {
            throw new IllegalArgumentException(String.format(ACCOUNT_NOT_EXISTS, "l'information", "transmise"));
        }
        String encodedPassword = bCryptPasswordEncoder.encode(passwordData.getPassword());
        profile.setPassword(encodedPassword);
        confirmationToken.setConfirmedAt(Instant.now());
    }

    public void register(Profile profile) {
        Objects.requireNonNull(profile.getFirstName(), String.format(MISSING_FIELD, "Prénom"));
        Objects.requireNonNull(profile.getLastName(), String.format(MISSING_FIELD, "Nom"));
        Objects.requireNonNull(profile.getPassword(), String.format(MISSING_FIELD, "mode de passe"));

        if (commonsMethods.stringIsNullOrEmpty(profile.getEmail()) && commonsMethods.stringIsNullOrEmpty(profile.getPhone())) {
            Objects.requireNonNull(profile.getPhone(), String.format(MISSING_FIELD, "Téléphone"));
            Objects.requireNonNull(profile.getEmail(), String.format(MISSING_FIELD, "mail"));
            if (!userNameValidator.test(profile.getEmail())) {
                throw new IllegalArgumentException(String.format(EMAIL_INVALID, profile.getEmail()));
            }
        }

        Profile profileByPhone = profilRepository.findByPhone(profile.getPhone()).orElse(null);
        if (profileByPhone != null) {
            throw new IllegalArgumentException(String.format(ACCOUNT_EXISTS, "le téléphone", profile.getPhone()));
        }

        Profile profileByEmail = profilRepository.findByEmail(profile.getEmail()).orElse(null);
        if (profileByEmail != null) {
            throw new IllegalArgumentException(String.format(ACCOUNT_EXISTS, "le mail", profile.getEmail()));
        }

        if (profile.getRoles() == null) {
            profile.setRoles(Set.of(UserRole.USER));
        }

        String encodedPassword = bCryptPasswordEncoder.encode(profile.getPassword());
        profile.setPassword(encodedPassword);
        profileService.register(profile);
    }

    public AuthenticationData login(AuthenticationRequest authenticationRequest) {
        String usermame = authenticationRequest.getPhone();
        String password = authenticationRequest.getPassword();
        Objects.requireNonNull(password, String.format(MISSING_FIELD, "mode de passe"));
        Objects.requireNonNull(usermame, String.format(MISSING_FIELD, "mail"));

        Optional<Profile> optionalProfile = profilRepository.findByPhoneAndPhoneIndex(authenticationRequest.getPhone(), authenticationRequest.getPhoneIndex());
        if (optionalProfile.isEmpty()) {
            throw new IllegalArgumentException(String.format(ACCOUNT_NOT_EXISTS, "l'email", usermame));
        }

        return this.getAuthenticationData(optionalProfile.get());
    }

    public void activate(ActivationData activationData) {
        Objects.requireNonNull(activationData.getToken(), String.format(MISSING_FIELD, "token"));
        ConfirmationToken confirmationToken = confirmationTokenService.activateByToken(
                activationData.getToken()
        );
        Profile profile = confirmationToken.getProfile();

        Query query = new Query();
        query.addCriteria(
                Criteria.where("phone").is(profile.getPhone())
                        .orOperator(Criteria.where("email").is(profile.getEmail()))
        );

        Profile inactiveProfile = this.mongoTemplate.findOne(query, Profile.class);
        inactiveProfile.setActive(TRUE);
        this.mongoTemplate.save(inactiveProfile);
    }

    public void resetPasswordLink(String email) {
        if (!userNameValidator.test(email)) {
            throw new IllegalArgumentException(String.format(EMAIL_INVALID, email));
        }

        Optional<Profile> optionalProfile = profilRepository.findByEmail(email);
        if (optionalProfile.isEmpty()) {
            throw new IllegalArgumentException(String.format(ACCOUNT_NOT_EXISTS, "l'email", email));
        }
        if (sendAccountActivationCode) {
            this.confirmationTokenService.sendActivationCode(optionalProfile.get());
        }
    }

    @Override
    public void phoneActivationCode(ActivationData activationData) {
        Profile currentProfile = null;
        try {
            currentProfile = profileService.findByPhoneAndPhoneIndex(activationData.getPhone(), activationData.getPhoneIndex());
        } catch (UsernameNotFoundException exception) {
            log.info("Nouvel utilisateur avec {} {}", activationData.getPhoneIndex(), activationData.getPhone());
        }
        if (currentProfile == null) {
            currentProfile = new Profile();
            currentProfile.setPhoneIndex(activationData.getPhoneIndex());
            currentProfile.setPhone(activationData.getPhone());
        }
        this.profileService.register(currentProfile);
        if (sendAccountActivationCode) {
            this.confirmationTokenService.sendActivationCode(activationData);

        }
    }

    @Override
    public AuthenticationData updateProfile(Profile profile) {
        Profile currentProfile = profileService.findByPhoneAndPhoneIndex(profile.getPhone(), profile.getPhoneIndex());
        currentProfile.setPhoneActive(TRUE);
        currentProfile.setActive(TRUE);
        currentProfile.setFirstName(profile.getFirstName());
        currentProfile.setLastName(profile.getLastName());
        currentProfile.setEmail(profile.getEmail());

        Profile updatedProfile = this.profileService.update(currentProfile);
        return getAuthenticationData(updatedProfile);
    }

    @Override
    public AuthenticationData refreshToken(Map<String, String> data) {
        AuthenticationData authenticationData = this.authenticationDataService.findByRefreshoken(data.get(refreshToken));
        Profile profile = this.profileService.findById(authenticationData.getUserId());
        return this.getAuthenticationData(profile);
    }

    @Override
    public AuthenticationData activatePhone(ActivationData activationData) {
        AuthenticationData authenticationData = new AuthenticationData();
        Profile profile = this.profileService.findByPhoneAndPhoneIndex(activationData.getPhone(), activationData.getPhoneIndex());
        String activationStatus = null;
        if (sendAccountActivationCode) {
            activationStatus = this.confirmationTokenService.activatePhone(activationData);
        } else if (activationData.getToken().equals(accountActivationCode)) {
            activationStatus = "approved";
        }
        if (!activationStatus.equals("approved")) {
            throw new IllegalArgumentException(CODE_INVALID);
        }
        profile.setPhoneActive(TRUE);
        Profile updatedProfile = this.profileService.update(profile);
        if (!Strings.isNullOrEmpty(updatedProfile.getLastName()) || !Strings.isNullOrEmpty(updatedProfile.getFirstName())) {
            authenticationData = this.getAuthenticationData(updatedProfile);
        }
        return authenticationData;
    }

    private AuthenticationData getAuthenticationData(Profile updatedProfile) {
        AuthenticationData authenticationData = new AuthenticationData();
        String accessToken = this.jwtTokenUtil.generateToken(updatedProfile);
        String refreshToken = commonsMethods.getRefreshToken();
        authenticationData.setCreation(Instant.now());
        authenticationData.setAccessToken(accessToken);
        authenticationData.setRefreshToken(refreshToken);
        authenticationData.setUserId(updatedProfile.getId());
        return this.authenticationDataService.create(authenticationData);
    }
}
