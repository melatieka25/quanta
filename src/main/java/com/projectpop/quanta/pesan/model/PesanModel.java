package com.projectpop.quanta.pesan.model;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateCreated;

    @ManyToOne
    @JoinColumn(name="siswa_id", nullable=false)
    private SiswaModel siswaPesan;
}