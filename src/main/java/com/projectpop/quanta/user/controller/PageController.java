package com.projectpop.quanta.user.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserService;

@Controller
public class PageController {

    @Autowired
    ServerProperties serverProperties;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;
    
    @RequestMapping("/")
    public String home() {
        return "home";
    }

    @RequestMapping("/login")
    public String login(Model model){
        model.addAttribute("port", serverProperties.getPort());
        return "auth/login";
    }

    @GetMapping (value = "/logout-sso")
    public ModelAndView logoutSSO(Principal principal) {
        UserModel user = userService.getUserByEmail(principal.getName());
        System.out.println(user.getEmail());
        
        return new ModelAndView("redirect:/logout");
    }


}
