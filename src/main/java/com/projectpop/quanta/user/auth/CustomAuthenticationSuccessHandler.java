package com.projectpop.quanta.user.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserService;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserModel user = userService.getUserByEmail(authentication.getName());

        if (user.getIsPassUpdated()) {
            response.sendRedirect("/?loginSuccess");
        } else {
            response.sendRedirect("/?firstLoginSuccess");
        }
    }
}
