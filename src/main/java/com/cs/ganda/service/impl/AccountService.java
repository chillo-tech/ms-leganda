package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Profile;
import com.cs.ganda.repository.ProfileRepository;
import com.cs.ganda.service.CommonsMethods;
import com.cs.ganda.service.ProfileService;
import com.cs.ganda.service.sms.ConfirmationTokenService;
import com.cs.ganda.validators.UserNameValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class AccountService {
    private static final String EMAIL_INVALID = "l'email %s est invalide";
    private static final String MISSING_FIELD = "Merci de sasir le %s";
    private static final String ACCOUNT_EXISTS = "Un compte avec %s %s existe. Avez vous oublié votre mot de passe ?";
    private static final String ACCOUNT_NOT_EXISTS = "Aucun compte ne correspond à %s %s.";
    private final ProfileService profileService;
    private final ProfileRepository profilRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final CommonsMethods commonsMethods;
    private final UserNameValidator userNameValidator;

    public void activate(ActivationData activationData) {
        Profile profile = this.profileService.findByPhoneAndPhoneIndex(activationData.getPhone(), activationData.getPhoneIndex());
        Objects.requireNonNull(activationData.getToken(), String.format(MISSING_FIELD, "token"));
        this.confirmationTokenService.activate(activationData);
        profile.setActive(Boolean.TRUE);
        this.profilRepository.save(profile);
    }

    public void register(Profile profile) {
        Objects.requireNonNull(profile.getFirstName(), String.format(MISSING_FIELD, "Prénom"));
        Objects.requireNonNull(profile.getLastName(), String.format(MISSING_FIELD, "Nom"));
        if (this.commonsMethods.stringIsNullOrEmpty(profile.getEmail()) && this.commonsMethods.stringIsNullOrEmpty(profile.getPhone())) {
            Objects.requireNonNull(profile.getPhone(), String.format(MISSING_FIELD, "Téléphone"));
            Objects.requireNonNull(profile.getEmail(), String.format(MISSING_FIELD, "mail"));
            if (!this.userNameValidator.test(profile.getEmail())) {
                throw new IllegalArgumentException(String.format(EMAIL_INVALID, profile.getEmail()));
            }
        }

        boolean userWithPhoneExists = this.profilRepository.findTopByPhoneAndPhoneIndex(profile.getPhone(), profile.getPhoneIndex()).isPresent();
        if (!userWithPhoneExists) {
            this.profileService.register(profile);
        } else {
            this.confirmationTokenService.sendActivationCode(profile);
        }
    }

}
