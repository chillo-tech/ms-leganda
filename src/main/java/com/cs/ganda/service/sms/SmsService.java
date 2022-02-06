package com.cs.ganda.service.sms;

import com.cs.ganda.exception.ResourceNotFoundException;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class SmsService {
    private static final String VALIDATION_CODE_NOT_EXISTS = "Le code saisi est invalide.";
    @Value("${spring.sms.twilo.sid}")
    String ACCOUNT_SID;
    @Value("${spring.sms.twilo.token}")
    String AUTH_TOKEN;
    @Value("${spring.sms.twilo.vaid}")
    String VA_ID;

    @Async
    public void send(String index, String number, String activationCode) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Verification verification = Verification.creator(
                        VA_ID,
                        index + number,
                        "sms")
                .setSendDigits(activationCode)
                .create();
        log.info("[Activation code] status {}", verification.getStatus());

    }

    public VerificationCheck validate(String index, String number, String token) {
        VerificationCheck verificationCheck = null;
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            verificationCheck = VerificationCheck.creator(
                            VA_ID,
                            token
                    ).setTo(index + number)
                    .create();
        } catch (ApiException e) {
            log.error("", e);
            throw new ResourceNotFoundException(VALIDATION_CODE_NOT_EXISTS);
        }
        return verificationCheck;
    }
}
