package com.projectpop.quanta.siswakelas.model;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "siswa_kelas")
public class SiswaKelasModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name= "siswa_id")
    private SiswaModel siswa;

    @ManyToOne
    @JoinColumn(name= "kelas_id")
    private KelasModel kelasSiswa;
}
