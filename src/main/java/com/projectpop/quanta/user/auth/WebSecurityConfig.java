package com.projectpop.quanta.user.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/konsultasi/admin/**", "/statistics", "/statistics/**").hasRole("ADMIN")
                .antMatchers("/konsultasi/siswa/**").hasRole("SISWA")
                .antMatchers("/konsultasi/pengajar/**").hasRole("PENGAJAR")
                .antMatchers("/konsultasi/**").hasAnyRole("ADMIN", "SISWA", "PENGAJAR")
                .antMatchers("/presensi/**").hasAnyRole("PENGAJAR", "ADMIN")
                .antMatchers("/presensi").hasAnyRole("PENGAJAR", "ADMIN")
                .antMatchers("/api/**").permitAll()
                .antMatchers("/jadwal-kelas/add/**", "/jadwal-kelas/delete/**", "/jadwal-kelas/update/**").hasRole("ADMIN")
                .antMatchers("/pengajar").hasRole("ADMIN")
                .antMatchers("/pengajar/**").hasRole("ADMIN")
                .antMatchers("/siswa").hasRole("ADMIN")
                .antMatchers("/siswa/**").hasRole("ADMIN")
                .antMatchers("/ortu").hasRole("ADMIN")
                .antMatchers("/ortu/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler)
				.usernameParameter("email")
				.permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").permitAll();
        return http.build();
    }

    public BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

}