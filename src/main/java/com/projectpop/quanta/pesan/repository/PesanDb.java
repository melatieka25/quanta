package com.projectpop.quanta.pesan.repository;

import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PesanDb extends JpaRepository<PesanModel, String> {
    Optional<List<PesanModel>> findAllBySiswaPesan(SiswaModel siswaModel);
    List<PesanModel> findAllByUser(UserModel userModel);
}
