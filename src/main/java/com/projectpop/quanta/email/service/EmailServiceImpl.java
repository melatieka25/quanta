package com.projectpop.quanta.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String emailPenerima, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("quantum.research.assistant@gmail.com");
        message.setTo(emailPenerima);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);

        System.out.println("Mail Sent successfully...");
    }
}
