package com.projectpop.quanta.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    @Override
    public void sendEmail(ArrayList<String> emailPenerimaArrayList, String subject, String body) {
        String[] emailPenerima = emailPenerimaArrayList.toArray(new String[emailPenerimaArrayList.size()]);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("quantum.research.assistant@gmail.com");
        message.setTo(emailPenerima);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);

        System.out.println("Mail Sent successfully...");
    }
}
