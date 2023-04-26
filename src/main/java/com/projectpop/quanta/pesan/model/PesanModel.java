package com.projectpop.quanta.pesan.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.user.model.UserModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pesan")
public class PesanModel implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @JsonIgnore
    @NotNull
    @Column(name = "msg", nullable = false)
    private String msg;

    @JsonIgnore
    @NotNull
    @Column(name="date_created", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateCreated;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="siswa_id", nullable=false)
    private SiswaModel siswaPesan;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserModel user;
}
