package com.cs.ganda.service.sms;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.document.Profile;
import com.cs.ganda.repository.ConfirmationTokenRepository;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {

    private static final String ACCOUNT_NOT_EXISTS = "Aucun compte ne correspond à %s %s";
    private final ConfirmationTokenRepository confirmationTokenRepository;
    @Value("${spring.sms.twilo.sid}")
    String ACCOUNT_SID;
    @Value("${spring.sms.twilo.token}")
    String AUTH_TOKEN;
    @Value("${spring.sms.twilo.vaid}")
    String VA_ID;

    @Async
    public void sendActivationCode(Profile profile) {
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
    }


    public void activate(ActivationData activationData) {
        Optional<ConfirmationToken> optionalConfirmationToken =
                this.confirmationTokenRepository.findTopByProfilePhoneAndProfilePhoneIndexOrderByCreationDesc(
                        activationData.getPhone(),
                        activationData.getPhoneIndex()
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
                            activationData.getToken()
                    ).setTo(activationData.getPhoneIndex() + activationData.getPhone())
                    .create();

            confirmationToken.getProfile().setActive(Boolean.TRUE);
            confirmationToken.setConfirmedAt(Instant.now());
            log.info("Statut de verification {}", verificationCheck.getStatus());
        } catch (Exception e) {
            log.error("Erreur {}", e);
        }
    }


}
