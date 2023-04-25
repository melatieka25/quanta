package com.projectpop.quanta.user.auth;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordManager {

    public static String encrypt(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        return hashedPassword;
    }


    // Sumber: https://www.baeldung.com/java-generate-secure-password
    public static String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(33, 45)
            .build();
        return pwdGenerator.generate(length);
    }

    public static String generateRandomNumbers(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(48, 57)
            .build();
        return pwdGenerator.generate(length);
    }

    public static String generateRandomAlphabet(int length, boolean isUppercase) {
        RandomStringGenerator pwdGenerator = null;
        if (isUppercase){
            pwdGenerator = new RandomStringGenerator.Builder().withinRange(65, 90)
            .build();
        } else {
            pwdGenerator = new RandomStringGenerator.Builder().withinRange(97, 122)
            .build();
        }
       
        return pwdGenerator.generate(length);
    }

    public static String generateCommonTextPassword() {
        String pwString = generateRandomSpecialCharacters(2).concat(generateRandomNumbers(2))
          .concat(generateRandomAlphabet(2, true))
          .concat(generateRandomAlphabet(2, false));
        List<Character> pwChars = pwString.chars()
          .mapToObj(data -> (char) data)
          .collect(Collectors.toList());
        Collections.shuffle(pwChars);
        String password = pwChars.stream()
          .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
          .toString();
        return password;
    }

}
