package com.projectpop.quanta.kelas.model;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;
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
    private Jenjang jenjang;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private JadwalAvail days;

    @ManyToOne
    @JoinColumn(name="kakak_asuh_id", nullable=false)
    private PengajarModel kakakAsuh;

    @OneToMany(mappedBy = "kelas")
    private List<JadwalKelasModel> listJadwalKelas;

    @OneToMany(mappedBy = "kelasSiswa")
    private List<SiswaKelasModel> listSiswaKelas;

    @ManyToOne
    @JoinColumn(name="tahun_ajar_id", nullable = false)
    private TahunAjarModel tahunAjar;
}
