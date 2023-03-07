package com.projectpop.quanta.konsultasi.model;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private LocalDateTime startTime;

    @NotNull
    @Column(name="end_time", nullable = false)
    private LocalDateTime endTime;

    @NotNull
    @Column(name="duration", nullable = false)
    private Integer duration;

    @NotNull
    @Column(nullable = false)
    private String topic;

    @NotNull
    @Column(nullable = false)
    private String room;

    @NotNull
    @Column(nullable = false)
    private StatusKonsul status;

    @ManyToOne
    @JoinColumn(name="mapel_id")
    private MataPelajaranModel mapelKonsul;

    @ManyToOne
    @JoinColumn(name="pengajar_id")
    private PengajarModel pengajarKonsul;

    @ManyToOne
    @JoinColumn(name="tahun_ajar_id")
    private TahunAjarModel tahunAjarKonsul;

    @OneToMany(mappedBy = "konsultasi", cascade = CascadeType.ALL)
    private List<SiswaKonsultasiModel> listSiswaKonsultasi;

}
