package com.projectpop.quanta.email.service;

public interface EmailService {
    void sendEmail(String emailPenerima, String subject, String body);
}
