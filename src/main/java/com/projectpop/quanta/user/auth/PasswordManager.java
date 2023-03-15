package com.projectpop.quanta.user.auth;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordManager {
    //Referensi: https://codescracker.com/java/program/java-check-password-strength.htm
    public static boolean validationChecker(String password){
        int passwordLength = password.length();
        int upChars = 0, lowChars = 0, digits = 0, special = 0;
        if(passwordLength < 8) {
            return false;
        } else {
            for(int i=0; i<passwordLength; i++) {
                Character ch = password.charAt(i);
                if(Character.isUpperCase(ch))
                    upChars = 1;
                else if(Character.isLowerCase(ch))
                    lowChars = 1;
                else if(Character.isDigit(ch))
                    digits = 1;
                else
                    special = 1;
            }
        }
        if(upChars==1 && lowChars==1 && digits==1 && special==1)
            return true;
        else
            return false;
    }

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
