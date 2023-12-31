package com.projectpop.quanta.konsultasi.model;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.sun.istack.NotNull;
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
@Entity
@Table(name = "konsultasi")
public class KonsultasiModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",  updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(name="start_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @NotNull
    @Column(name="end_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotNull
    @Column(name="duration", nullable = false)
    private Integer duration;

    @NotNull
    @Column(nullable = false)
    private String topic;

    @Column()
    private String place;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private StatusKonsul status;

    @NotNull
    @Column(name="jenjang_kelas", nullable = false)
    @Enumerated(value=EnumType.STRING)
    private Jenjang jenjangKelas;

    @ManyToOne
    @JoinColumn(name="mapel_id")
    private MataPelajaranModel mapelKonsul;

    @ManyToOne
    @JoinColumn(name="pengajar_id")
    private PengajarModel pengajarKonsul;

    @ManyToOne
    @JoinColumn(name="tahun_ajar_id")
    private TahunAjarModel tahunAjarKonsul;

    @OneToMany(mappedBy = "konsultasi")
    private List<SiswaKonsultasiModel> listSiswaKonsultasi;

    @ManyToOne
    @JoinColumn(name="siswa_id")
    private SiswaModel dibuatOleh;

    @NotNull
    @Column(name="created_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdTime;

    @NotNull
    @Column(name="expired_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiredTime;

    @Column(name="rejected_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime rejectedTime;

    @Column(name="accepted_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime acceptedTime;

    @Column(name="closed_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime closedTime;

    @Column()
    private String rejectionReason;
}
