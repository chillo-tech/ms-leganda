package com.cs.ganda.service.emails;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Email;
import com.cs.ganda.document.Profile;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class MailsService {
    private final BaseEmails baseEmails;
    private final MailsSender mailSender;

    @Async
    public void newPublication(Ad ad) {
        Email email = this.baseEmails.newPublication(ad);
        mailSender.send(email);
    }
    /**/
    @Async
    public void newPublication(Ad ad,String emailDestinataire) {
        Email email = this.baseEmails.newPublication(ad,emailDestinataire);
        mailSender.send(email);
    }
    /**/

    @Async
    public void newProfile(Profile profile, String activationCode) {
        Email email = this.baseEmails.newProfile(profile, activationCode);
        mailSender.send(email);
    }

}
