package com.projectpop.quanta.mapel.model;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mata_pelajaran")
public class MataPelajaranModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String abbr;

    @OneToMany(mappedBy = "mapel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PengajarMapelModel> listPengajarMapel;

    @OneToMany(mappedBy = "mapelJadwal")
    private List<JadwalKelasModel> jadwal;

    @OneToMany(mappedBy = "mapelKonsul")
    private List<KonsultasiModel> konsultasi;


}
