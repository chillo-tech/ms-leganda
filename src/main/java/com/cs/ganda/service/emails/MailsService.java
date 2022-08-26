package com.cs.ganda.service.emails;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Email;
import com.cs.ganda.document.Profile;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class MailsService {
    private final BaseEmails baseEmails;
    private final MailsSender mailSender;

    @Async
    public void newPublication(final Ad ad) {
        final Email email = this.baseEmails.newPublication(ad);
        this.mailSender.send(email);
    }

    /**/
    @Async
    public void newPublication(final Ad ad, final List<String> emailDestinataire) {
        final Email email = this.baseEmails.newPublication(ad, emailDestinataire);
        this.mailSender.send(email);
    }
    /**/

    @Async
    public void newProfile(final Profile profile, final String activationCode) {
        final Email email = this.baseEmails.newProfile(profile, activationCode);
        this.mailSender.send(email);
    }

}
