package com.projectpop.quanta.siswa.model;

import com.projectpop.quanta.kelas.model.KelasModel;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Jenjang jenjang;

    @NotNull
    @Column(nullable = false)
    private String sekolah;

    @JsonIgnore
    @OneToMany(mappedBy = "siswaKonsul", cascade = CascadeType.ALL)
    private List<SiswaKonsultasiModel> listKonsultasiSiswa;

    @JsonIgnore
    @OneToMany(mappedBy = "siswa", cascade = CascadeType.ALL)
    private List<PresensiModel> listPresensiSiswa;

    @JsonIgnore
    @OneToMany(mappedBy = "siswa", cascade = CascadeType.ALL)
    private List<SiswaJadwalModel> listJadwalSiswa;

    @JsonIgnore
    @OneToMany(mappedBy = "siswa", cascade = CascadeType.ALL)
    private List<SiswaKelasModel> listKelasSiswa;

    @JsonIgnore
    @OneToMany(mappedBy = "siswaPesan", cascade = CascadeType.ALL)
    private List<PesanModel> listPesanSiswa;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name= "ortu_id")
    private OrtuModel ortu;

    private transient KelasModel kelasBimbel;
    private transient int ortuId;
}