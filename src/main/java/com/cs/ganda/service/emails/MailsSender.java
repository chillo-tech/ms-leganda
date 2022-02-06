package com.cs.ganda.service.emails;

import com.cs.ganda.document.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Slf4j
@Component
public class MailsSender {

    private final JavaMailSender mailSender;

    public MailsSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(Email eParams) {
        if (eParams.isHtml()) {
            try {
                sendHtmlMail(eParams);
            } catch (MessagingException e) {
                log.error("Could not send email to : {} Error = {}", eParams.getToAsList(), e.getMessage());
            }
        } else {
            sendPlainTextMail(eParams);
            eParams.setTo(Collections.singletonList(eParams.getFrom()));
            sendPlainTextMail(eParams);
        }
    }

    private void sendHtmlMail(Email eParams) throws MessagingException {
        boolean isHtml = true;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED,
                UTF_8.name());


        //helper.addAttachment("facebook-icon", new ClassPathResource("static/images/facebook-icon.gif"));

        helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
        helper.setReplyTo(eParams.getFrom());
        helper.setFrom(eParams.getFrom());
        helper.setSubject(eParams.getSubject());
        helper.setText(eParams.getMessage(), isHtml);
        if (eParams.getCc().size() > 0) {
            helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
        }
        mailSender.send(message);
    }

    private void sendPlainTextMail(Email eParams) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        eParams.getTo().toArray(new String[eParams.getTo().size()]);
        mailMessage.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
        mailMessage.setReplyTo(eParams.getFrom());
        mailMessage.setFrom(eParams.getFrom());
        mailMessage.setSubject(eParams.getSubject());
        mailMessage.setText(eParams.getMessage());
        if (eParams.getCc().size() > 0) {
            mailMessage.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
        }
        mailSender.send(mailMessage);
    }
}
