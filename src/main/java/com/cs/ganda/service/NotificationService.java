package com.cs.ganda.service;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Profile;
import com.cs.ganda.service.emails.MailsService;
import com.cs.ganda.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {
    private final MailsService mailService;
    private final SmsService smsService;
    private String sms;

    public NotificationService(final MailsService mailService, final SmsService smsService) {
        this.mailService = mailService;
        this.smsService = smsService;
    }

    @Async
    public void newAdNotificationToCustomers(final List<Profile> profileList, final Ad ad) {
        final List<String> emails = profileList.stream().map(Profile::getEmail).collect(Collectors.toList());
        final List<String> phones = profileList.stream().map(Profile::getFullPhone).collect(Collectors.toList());
        this.mailService.newPublication(ad, emails);
        this.smsService.newPublication(ad, phones);

    }
}