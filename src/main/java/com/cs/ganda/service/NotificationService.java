package com.cs.ganda.service;

import com.cs.ganda.document.Ad;
import com.cs.ganda.document.Profile;
import com.cs.ganda.service.emails.MailsService;
import com.cs.ganda.service.sms.SmsService;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class NotificationService {
    private final MailsService mailService;
    private final SmsService smsService;

    private final ProfileService profileService;

    @Async
    public void newAdNotificationToCustomers(final Ad ad) {
        final List<Profile> profileList = this.profileService.findByAddress(ad.getAddress());
        final List<String> emails = profileList.stream().map(Profile::getEmail).filter(email -> !Strings.isNullOrEmpty(email)).collect(Collectors.toList());
        final List<String> phones = profileList.stream().map(profile -> profile.getPhoneIndex() + profile.getPhone()).filter(phone -> !Strings.isNullOrEmpty(phone)).collect(Collectors.toList());
        if (!emails.isEmpty()) {
            this.mailService.newPublication(ad, emails);
        }
        if (!phones.isEmpty()) {
            this.smsService.newPublication(ad, phones);
        }

    }
}
