package com.projectpop.quanta.siswa.model;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.user.model.UserModel;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "siswa")
@PrimaryKeyJoinColumn(name = "user_id")
public class SiswaModel extends UserModel {

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Jurusan jurusan;

    @NotNull
    @Column(nullable = false)
    private Integer kelas;

    @NotNull
    @Column(nullable = false)
    private String sekolah;

    @OneToMany(mappedBy = "siswaKonsul", cascade = CascadeType.ALL)
    private List<SiswaKonsultasiModel> listKonsultasiSiswa;

    @OneToMany(mappedBy = "siswa", cascade = CascadeType.ALL)
    private List<PresensiModel> listPresensiSiswa;

    @OneToMany(mappedBy = "siswa", cascade = CascadeType.ALL)
    private List<SiswaJadwalModel> listJadwalSiswa;

    @OneToMany(mappedBy = "siswa", cascade = CascadeType.ALL)
    private List<SiswaKelasModel> listKelasSiswa;

    @OneToMany(mappedBy = "siswaPesan", cascade = CascadeType.ALL)
    private List<PesanModel> listPesanSiswa;

    @ManyToOne
    @JoinColumn(name= "ortu_id")
    private OrtuModel ortu;

    private transient String kelasBimbel;
}

