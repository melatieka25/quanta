package com.projectpop.quanta.presensi.model;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "presensi")
public class PresensiModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private PresensiStatus status;

    @ManyToOne
    @JoinColumn(name = "siswa")
    private SiswaModel siswa;

    @ManyToOne
    @JoinColumn(name = "jadwal_kelas")
    private JadwalKelasModel jadwal;
}
