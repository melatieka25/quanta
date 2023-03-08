package com.projectpop.quanta.siswajadwalkelas.model;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "siswa_jadwal")
public class SiswaJadwalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name= "siswa_id")
    private SiswaModel siswa;

    @ManyToOne
    @JoinColumn(name= "jadwal_id")
    private JadwalKelasModel jadwalKelas;
}
