package com.projectpop.quanta.email.service;

import java.util.ArrayList;

public interface EmailService {
    void sendEmail(String emailPenerima, String subject, String body);
    void sendEmail(ArrayList<String> emailPenerima, String subject, String body);
}
