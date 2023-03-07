package com.projectpop.quanta.pesan.model;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pesan")
public class PesanModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "msg", nullable = false)
    private String msg;

    @NotNull
    @Column(name="date_created", nullable = false)
    private LocalDateTime dateCreated;
}
