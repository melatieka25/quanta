package com.projectpop.quanta.pengajarmapel.model;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "pengajar_mapel")
public class PengajarMapelModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name= "mapel_id")
    private MataPelajaranModel mapel;

    @ManyToOne
    @JoinColumn(name= "pengajar_id")
    private PengajarModel pengajar;

}
