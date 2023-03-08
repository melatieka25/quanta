package com.projectpop.quanta.tahunajar.model;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tahun_ajar")
public class TahunAjarModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(name="start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @Column(name="end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    @Column(nullable = false)
    private Boolean isAktif;

    @OneToMany(mappedBy = "tahunAjarKonsul", cascade = CascadeType.ALL)
    private List<KonsultasiModel> listKonsultasi;

    @OneToMany(mappedBy = "tahunAjar", cascade = CascadeType.ALL)
    private List<KelasModel> listKelas;
}
