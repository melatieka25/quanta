package com.projectpop.quanta.jadwalkelas.model;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jadwal_kelas")
public class JadwalKelasModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateClass;

    @NotNull
    @Column(name = "end_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateClass;

    @NotNull
    @Column(nullable = false)
    private String ruangKelas;

    @ManyToOne
    @JoinColumn(name="mapel_id")
    private MataPelajaranModel mapelJadwal;

    @ManyToOne
    @JoinColumn(name="pengajar_id")
    private PengajarModel pengajarKelas;

    @ManyToOne
    @JoinColumn(name="kelas_id")
    private KelasModel kelas;

    @OneToMany(mappedBy = "jadwal", cascade = CascadeType.ALL)
    private List<PresensiModel> listPresensi;

    @OneToMany(mappedBy = "jadwalKelas", cascade = CascadeType.ALL)
    private List<SiswaJadwalModel> listSiswaJadwal;

    @NotNull
    @Column(name = "isi_presensi_able", nullable = false)
    private Boolean isiPresensi = false;

}
