package com.projectpop.quanta.user.model;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.pesan.model.PesanModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Long phone_num;

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

    @Lob
    @NotNull
    @Column(name = "password_pertama", nullable = false)
    private String passwordPertama;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @JsonIgnore
    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Religion religion;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PesanModel> listPesan;

    public String getNameEmail() {
        return this.name + " (" + this.email + ") ";
    }
}
