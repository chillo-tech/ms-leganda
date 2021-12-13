package com.cs.ganda.service.sms;

import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.document.Profile;
import com.cs.ganda.enums.Status;
import com.cs.ganda.exception.ResourceNotFoundException;
import com.cs.ganda.repository.ConfirmationTokenRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {

    private static final String ACCOUNT_NOT_EXISTS = "Aucun compte ne correspond à %s %s";
    private static final String INVALID_PHONE_NUMBER = "Votre numéro de téléphone est invalide";
    private static final String VALIDATION_CODE_NOT_EXISTS = "Le code saisi est invalide.";
    private final ConfirmationTokenRepository confirmationTokenRepository;
    @Value("${spring.sms.twilo.sid}")
    String ACCOUNT_SID;
    @Value("${spring.sms.twilo.token}")
    String AUTH_TOKEN;
    @Value("${spring.sms.twilo.vaid}")
    String VA_ID;

    public void sendActivationCode(Profile profile) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Verification verification = Verification.creator(
                            VA_ID,
                            profile.getPhoneIndex() + profile.getPhone(),
                            "sms")
                    .create();

            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setToken(verification.getUrl().getRawQuery());
            confirmationToken.setProfile(profile);
            confirmationToken.setCreation(Instant.now());
            confirmationTokenRepository.save(confirmationToken);
        } catch (ApiException e) {
            log.error("{}", e);
            throw new ResourceNotFoundException(INVALID_PHONE_NUMBER);
        }
    }

    public void sendAddActivationCode(String itemId, String phoneIndex, String phone, String adId) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Verification verification = Verification.creator(
                            VA_ID,
                            phoneIndex + phone,
                            "sms")
                    .create();

            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setToken(verification.getUrl().getRawQuery());
            confirmationToken.setPhone(phone);
            confirmationToken.setItemId(itemId);
            confirmationToken.setPhoneIndex(phoneIndex);
            confirmationToken.setAdId(adId);
            confirmationToken.setCreation(Instant.now());
            confirmationToken.setStatus(Status.ACTIVE);
            confirmationToken.setConfirmedAt(null);
            confirmationTokenRepository.save(confirmationToken);
        } catch (ApiException e) {
            log.error("{}", e);
            throw new ResourceNotFoundException(INVALID_PHONE_NUMBER);
        }
    }

    public ConfirmationToken activate(String itemId, String phone, String phoneIndex, String token) {
        Optional<ConfirmationToken> optionalConfirmationToken =
                this.confirmationTokenRepository.findTopByItemIdAndPhoneAndPhoneIndexAndStatus(
                        itemId,
                        phone,
                        phoneIndex,
                        Status.ACTIVE
                );
        if (optionalConfirmationToken.isEmpty()) {
            throw new IllegalStateException(String.format(ACCOUNT_NOT_EXISTS, "le téléphone", "fourni"));
        }
        ConfirmationToken confirmationToken = optionalConfirmationToken.get();
        if (Instant.now().isAfter(confirmationToken.getCreation().plus(15, ChronoUnit.MINUTES))) {
            log.warn("Le compte a été activé après 15 minutes");
        }

        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            VerificationCheck verificationCheck = VerificationCheck.creator(
                            VA_ID,
                            token
                    ).setTo(phoneIndex + phone)
                    .create();
            confirmationToken.setStatus(Status.CLOSED);
            confirmationToken.setConfirmedAt(Instant.now());
            log.info("Statut de verification {}", verificationCheck.getStatus());
            return this.confirmationTokenRepository.save(confirmationToken);
        } catch (ApiException e) {
            log.error("Erreur {}", e);
            throw new ResourceNotFoundException(VALIDATION_CODE_NOT_EXISTS);
        }
    }


}
