package com.projectpop.quanta.user.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.repository.UserDb;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserDb userDb;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserModel> optionalUser = userDb.findByName(email);
        if(!optionalUser.isPresent())
            throw new UsernameNotFoundException("Email " + email + " not found!");
        UserModel user = optionalUser.get();
        
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return new User(user.getName(), user.getPassword(), grantedAuthorities);
    }
}
