package com.cs.ganda.service.sms;

import com.cs.ganda.document.Ad;
import com.cs.ganda.exception.ResourceNotFoundException;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


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
    public void send(final String index, final String number, final String activationCode) {
        log.info("ACCOUNT_SID {} AUTH_TOKEN {} VA_ID {}", this.ACCOUNT_SID, this.AUTH_TOKEN, this.VA_ID);
        Twilio.init(this.ACCOUNT_SID, this.AUTH_TOKEN);
        final Verification verification = Verification.creator(
                        this.VA_ID,
                        index + number,
                        "sms")
                .setSendDigits(activationCode)
                .create();
        log.info("[Activation code] status {}", verification.getStatus());
    }

    /**
     * @Async public void message(final String index, final String number, final String activationCode) {
     * <p>
     * Twilio.init(this.ACCOUNT_SID, this.AUTH_TOKEN);
     * final Verification verification = Verification.creator(
     * this.VA_ID,
     * index + number,
     * "sms")
     * .setSendDigits(activationCode)
     * .create();
     * log.info("[Activation code] status {}", verification.getStatus());
     * }
     **/

    public VerificationCheck validate(final String index, final String number, final String token) {
        VerificationCheck verificationCheck = null;
        try {
            Twilio.init(this.ACCOUNT_SID, this.AUTH_TOKEN);
            verificationCheck = VerificationCheck.creator(
                            this.VA_ID,
                            token
                    ).setTo(index + number)
                    .create();
        } catch (final ApiException e) {
            log.error("", e);
            throw new ResourceNotFoundException(VALIDATION_CODE_NOT_EXISTS);
        }
        return verificationCheck;
    }

    public void newPublication(final Ad ad, final List<String> phones) {
        String sms = "Bonjour,\n";
        sms += "une nouvelle annonce vient d'être créé près de chez vous.\n";
        sms += "titre de l'annonce :" + ad.getName() + "\n";
        sms += "description de l'annonce :" + ad.getDescription() + "\n";
        sms += "par :" + ad.getProfile().getLastName() + " " + ad.getProfile().getFirstName() + "\n\n\n";
        sms += "LE GANDA votre coin qualité ";

        final String finalSms = sms;
        Twilio.init(this.ACCOUNT_SID, this.AUTH_TOKEN);

        phones.stream().map(PhoneNumber::new).forEach(mappedPhone -> {
            final Message message = Message.creator(mappedPhone, "+761705745", finalSms)
                    .create();

            log.info(String.format("l'id du message envoyé à %s est %s", mappedPhone, message.getSid()));
        });
    }
}

