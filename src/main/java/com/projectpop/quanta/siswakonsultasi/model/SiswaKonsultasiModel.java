package com.projectpop.quanta.siswakonsultasi.model;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "siswa_konsultasi")
public class SiswaKonsultasiModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name= "siswa_id")
    private SiswaModel siswaKonsul;

    @ManyToOne
    @JoinColumn(name= "konsultasi_id")
    private KonsultasiModel konsultasi;
}
