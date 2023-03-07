package com.projectpop.quanta.user.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.*;
//import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String nickname;

    @NotNull
    @Column(nullable = false)
    private Integer phone_num;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Gender gender;

    @NotNull
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(value=EnumType.STRING)
    private UserRole role;

    @Lob
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    private Boolean isPassUpdated;

    @NotNull
    @Column(nullable = false)
    private String address;

    @NotNull
    @Column(nullable = false)
    private String pob;

    @NotNull
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dob;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Religion religion;
}
