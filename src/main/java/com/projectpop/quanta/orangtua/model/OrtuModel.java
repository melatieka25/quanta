package com.projectpop.quanta.orangtua.model;

import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.user.model.UserModel;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "ortu")
@PrimaryKeyJoinColumn(name = "user_id")
public class OrtuModel extends UserModel {

    @NotNull
    @Column(nullable = false)
    private String job;

    @NotNull
    @Column(nullable = false)
    private String kantor;

    @OneToMany(mappedBy = "ortu")
    private List<SiswaModel> listAnak;

    private transient String passwordPertama;

}
