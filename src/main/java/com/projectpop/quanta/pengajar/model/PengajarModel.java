package com.projectpop.quanta.pengajar.model;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.user.model.UserModel;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "pengajar")
@PrimaryKeyJoinColumn(name = "user_id")
public class PengajarModel extends UserModel {
    @NotNull
    @Column(name = "ktp", nullable = false)
    private Long ktp;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private StatusPernikahan status;

    @NotNull
    @Column(name="last_edu" ,nullable = false)
    private Education lastEdu;

    @NotNull
    @Column(nullable = false)
    private String university;

    @NotNull
    @Column(nullable = false)
    private String jurusan;

    @NotNull
    @Column(nullable = false)
    private String position;

    @NotNull
    @Column(name="start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name="is_kakak_asuh", nullable = false)
    private Boolean isKakakAsuh;

    @OneToMany(mappedBy = "pengajar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<PengajarMapelModel> listPengajarMapel;

    @OneToMany(mappedBy = "kakakAsuh")
    private List<KelasModel> listKelasAsuh;

    @OneToMany(mappedBy="pengajarKonsul")
    private List<KonsultasiModel> listKonsultasiPengajar;

    @OneToMany(mappedBy = "pengajarKelas")
    private List<JadwalKelasModel> listJadwalKelas;
}
