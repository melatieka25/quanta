package com.projectpop.quanta.kelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jurusan;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
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
@Table(name = "kelas")
public class KelasModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Jurusan jurusan;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private JadwalAvail days;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="kakak_asuh_id", nullable=false)
    private PengajarModel kakakAsuh;

    @JsonIgnore
    @OneToMany(mappedBy = "kelas")
    private List<JadwalKelasModel> listJadwalKelas;

    @JsonIgnore
    @OneToMany(mappedBy = "kelasSiswa")
    private List<SiswaKelasModel> listSiswaKelas;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="tahun_ajar_id", nullable = false)
    private TahunAjarModel tahunAjar;
}
