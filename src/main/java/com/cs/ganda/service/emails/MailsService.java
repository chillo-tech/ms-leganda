package com.cs.ganda.service.emails;

import com.cs.ganda.document.Email;
import com.cs.ganda.document.Meal;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class MailsService {
    private final BaseEmails baseEmails;
    private final MailsSender mailSender;

    @Async
    public void newPublication(Meal meal) {
        Email email = this.baseEmails.newPublication(meal);
        mailSender.send(email);
    }

}
