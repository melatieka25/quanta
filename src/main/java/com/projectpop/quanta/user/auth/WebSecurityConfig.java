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
                .antMatchers("/statistik", "/statistik/**", "/mapel", "/mapel/**", "/kelas", "/kelas/**").hasRole("ADMIN")
                .antMatchers("/konsultasi/add", "/konsultasi/cancel/**", "/konsultasi/extend/**", "/konsultasi/ikuti/**").hasRole("SISWA")
                .antMatchers("/konsultasi/terima/**", "/konsultasi/tolak/**", "/konsultasi/close/**").hasRole("PENGAJAR")
                .antMatchers("/konsultasi/**").hasAnyRole("ADMIN", "SISWA", "PENGAJAR")
                .antMatchers("/presensi/**").hasAnyRole("PENGAJAR", "ADMIN")
                .antMatchers("/presensi").hasAnyRole("PENGAJAR", "ADMIN")
                .antMatchers("/api/**").permitAll()
                .antMatchers("/jadwal-kelas/add/**", "/jadwal-kelas/delete/**", "/jadwal-kelas/update/**").hasRole("ADMIN")
                .antMatchers("/pengajar").hasRole("ADMIN")
                .antMatchers("/pengajar/**").hasRole("ADMIN")
                .antMatchers("/siswa/all-rapor-siswa").hasAnyRole("ADMIN", "PENGAJAR")
                .antMatchers("/siswa/rapor-saya").hasRole("SISWA")
                .antMatchers("/siswa/add/rapor-siswa/**").hasAnyRole("PENGAJAR","ORTU")
                .antMatchers("/siswa/rapor-siswa/**").hasAnyRole("ADMIN", "PENGAJAR","ORTU")
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