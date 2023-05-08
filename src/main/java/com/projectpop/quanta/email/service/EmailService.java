package com.projectpop.quanta.email.service;

import com.projectpop.quanta.user.model.UserModel;

public interface EmailService {
    void sendEmail(String emailPenerima, String subject, String body);
    String getCredentialEmailBody(UserModel user);
}
