package com.projectpop.quanta.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.user.model.UserModel;

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
    public String getCredentialEmailBody(UserModel user) {
        String passwordWithX = user.getPasswordPertama().substring(0, 2) + "[XXXX]" + user.getPasswordPertama().substring(6);
        String passwordExample = user.getPasswordPertama().substring(0, 2) + "3002" + user.getPasswordPertama().substring(6);
        return "Halo " + user.getName() + ",\n\n" +
                  "Kami dengan senang hati ingin memberitahu Anda bahwa akun Anda telah berhasil dibuat pada sistem QUANTA (Quantum Assistant), lembaga bimbingan belajar siswa SMP dan SMA terkemuka.\n\n" +
                  "Berikut ini adalah detail akun Anda:\n\n" +
                  "Email: " + user.getEmail() + "\n" +
                  "Password: " + passwordWithX + "\n\n" +
                  "Harap perhatikan bahwa password Anda hanya terdiri dari 8 karakter dan \"[XXXX]\" dalam password tersebut merupakan kombinasi dari dua digit tanggal lahir dan dua digit bulan lahir Anda. Misalnya, jika Anda lahir pada tanggal 30 Februari, maka password Anda akan menjadi " + passwordExample + ". Penting untuk diingat bahwa password ini dibuat oleh sistem secara otomatis. Untuk menjaga keamanan akun Anda, kami sangat menyarankan Anda untuk segera mengubahnya setelah berhasil login.\n\n" +
                  "Untuk login ke akun Anda, silakan kunjungi http://localhost:8080.\n\n" +
                  "Jika Anda mengalami kendala atau memiliki pertanyaan lebih lanjut, jangan ragu untuk menghubungi tim Quantum Research Assistant. Kami akan dengan senang hati membantu Anda.\n\n" +
                  "Terima kasih atas kepercayaan Anda kepada Quantum Research. Kami berkomitmen untuk memberikan bimbingan terbaik dan mendukung kesuksesan Anda dalam perjalanan pendidikan.\n\n" +
                  "Salam hangat,\n" +
                  "Quantum Research Assistant";
    }
}
