package com.xperience.hero.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRSVPInvitation(String recipientEmail, String eventTitle, String inviteLink) {
        if (mailSender == null) {
            System.out.println("[EMAIL SKIPPED] To=" + recipientEmail + " Subject=Invited to " + eventTitle + " Link=" + inviteLink);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("You're invited to " + eventTitle);
        message.setText("You have been invited to attend " + eventTitle + ".\n\n" +
                "Click the link below to RSVP:\n" +
                inviteLink + "\n\n" +
                "Best regards,\nEvent RSVP Manager");

        try {
            mailSender.send(message);
            System.out.println("[EMAIL SENT] To=" + recipientEmail);
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] To=" + recipientEmail + " Reason=" + e.getMessage());
            System.err.println("[EMAIL ERROR] Check that MAIL_PASSWORD is a Gmail App Password (not your regular password).");
            System.err.println("[EMAIL ERROR] Invite link: " + inviteLink);
        }
    }
}
