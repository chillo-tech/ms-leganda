package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.document.Profile;
import com.cs.ganda.enums.Status;
import com.cs.ganda.exception.ResourceNotFoundException;
import com.cs.ganda.repository.ConfirmationTokenRepository;
import com.cs.ganda.service.ConfirmationTokenService;
import com.cs.ganda.service.emails.MailsService;
import com.cs.ganda.service.sms.SmsService;
import com.google.common.base.Strings;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private static final String ACCOUNT_NOT_EXISTS = "Aucun compte ne correspond à %s %s";
    private static final String INVALID_PHONE_NUMBER = "Votre numéro de téléphone est invalide";
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final SmsService smsService;
    private final MailsService mailsService;

    @Override
    public void sendActivationCode(ActivationData activationData) {
        String activationCode = RandomStringUtils.random(6, false, true);
        smsService.send(activationData.getPhoneIndex(), activationData.getPhone(), activationCode);
    }

    public void sendActivationCode(Profile profile) {
        try {
            String activationCode = RandomStringUtils.random(6, false, true);
            smsService.send(profile.getPhoneIndex(), profile.getPhone(), activationCode);
            if (!Strings.isNullOrEmpty(profile.getEmail())) {
                mailsService.newProfile(profile, activationCode);
            }
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setToken(activationCode);
            confirmationToken.setProfile(profile);
            confirmationToken.setStatus(Status.ACTIVE);
            confirmationToken.setCreation(Instant.now());
            confirmationTokenRepository.save(confirmationToken);
        } catch (ApiException e) {
            log.error("", e);
            throw new ResourceNotFoundException(INVALID_PHONE_NUMBER);
        }
    }

    public ConfirmationToken activate(String itemId, String phone, String phoneIndex, String token) {
        ConfirmationToken confirmationToken =
                this.confirmationTokenRepository
                        .findByTokenAndStatus(token, Status.ACTIVE)
                        .orElseThrow(() -> new IllegalStateException(String.format(ACCOUNT_NOT_EXISTS, "le téléphone", "fourni")));

        if (Instant.now().isAfter(confirmationToken.getCreation().plus(15, ChronoUnit.MINUTES))) {
            log.warn("Le compte a été activé après 15 minutes");
        }

        confirmationToken.setStatus(Status.CLOSED);
        confirmationToken.setConfirmedAt(Instant.now());
        VerificationCheck verificationCheck = this.smsService.validate(phoneIndex, phone, token);
        log.info("Statut de verification {}", verificationCheck.getStatus());
        return this.confirmationTokenRepository.save(confirmationToken);
    }

    public String activatePhone(ActivationData activationData) {
        VerificationCheck verificationCheck = this.smsService.validate(activationData.getPhoneIndex(), activationData.getPhone(), activationData.getToken());
        log.info("Statut de verification {}", verificationCheck.getStatus());
        return verificationCheck.getStatus();
    }

    public ConfirmationToken activateByToken(String token) {
        ConfirmationToken confirmationToken =
                this.confirmationTokenRepository
                        .findByTokenAndStatus(token, Status.ACTIVE)
                        .orElseThrow(() -> new IllegalStateException(String.format(ACCOUNT_NOT_EXISTS, "le téléphone", "fourni")));

        if (Instant.now().isAfter(confirmationToken.getCreation().plus(15, ChronoUnit.MINUTES))) {
            log.warn("Le compte a été activé après 15 minutes");
        }

        confirmationToken.setStatus(Status.CLOSED);
        confirmationToken.setConfirmedAt(Instant.now());
        this.smsService.validate(confirmationToken.getProfile().getPhoneIndex(), confirmationToken.getProfile().getPhone(), token);
        return this.confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken getConfirmationToken(String token) {
        Optional<ConfirmationToken> optionalConfirmationToken = this.confirmationTokenRepository.findByTokenAndStatus(token, Status.ACTIVE);
        if (optionalConfirmationToken.isEmpty()) {
            throw new IllegalStateException(String.format(ACCOUNT_NOT_EXISTS, "l'email", "fourni"));
        }
        ConfirmationToken confirmationToken = optionalConfirmationToken.get();
        if (Instant.now().isAfter(confirmationToken.getCreation().plus(15, ChronoUnit.MINUTES))) {
            log.warn("Le compte a été activé après 15 minutes");
        }

        return confirmationToken;
    }

}
