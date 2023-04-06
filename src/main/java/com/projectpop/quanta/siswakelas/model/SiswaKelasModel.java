package com.projectpop.quanta.siswakelas.model;

import com.projectpop.quanta.kelas.model.KelasModel;
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

    @ManyToOne(optional = false)
    @JoinColumn(name= "siswa_id")
    private SiswaModel siswa;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name= "kelas_id", referencedColumnName = "id")
    private KelasModel kelasSiswa;
}
