package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.admin.model.AdminModel;
import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.repository.JadwalKelasDb;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.service.PresensiService;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.user.model.UserModel;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserRole;
// import com.projectpop.quanta.admin.model.AdminModel;

import java.util.Optional;

import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


@Service
@Transactional
public class JadwalKelasServiceImpl implements JadwalKelasService{
    @Autowired
    JadwalKelasDb jadwalKelasDb;

    @Autowired
    PresensiService presensiService;

    @Autowired
    PengajarDb pengajarDb;

    @Autowired
    private PengajarService pengajarService;

    @Autowired
    private OrtuService ortuService;

    @Autowired
    private SiswaService siswaService;

    @Override
    public JadwalKelasModel getJadwalKelasById(Integer id) {
        return jadwalKelasDb.findById(id);
    }

    @Override
    public List<JadwalKelasModel> getListJadwalKelas() {
        return jadwalKelasDb.findAll();
    }

    public void createListPresensi(JadwalKelasModel jadwalKelas) {
        List<PresensiModel> listPresensi = new ArrayList<>();
        
        // create presensi per student
        // System.out.println("==== " + jadwalKelas.getKelas().getName());
        List<SiswaKelasModel> listSiswa = jadwalKelas.getKelas().getListSiswaKelas();
        for (SiswaKelasModel siswa : listSiswa) {
            System.out.println(siswa.getKelasSiswa().getName());
            PresensiModel presensi = presensiService.createPresensi(jadwalKelas);
            presensi.setSiswa(siswa.getSiswa());
            PresensiModel newPresensi = presensiService.updatePresensi(presensi);
            listPresensi.add(newPresensi);
        }
        jadwalKelas.setListPresensi(listPresensi);
    }

    @Override
    public void addJadwalKelas(JadwalKelasModel jadwalKelas) {
        createListPresensi(jadwalKelas);
        jadwalKelasDb.save(jadwalKelas);
    }

    @Override
    public void updateJadwalKelas(JadwalKelasModel jadwalKelas) {
        // System.out.println(jadwalKelas.getListPresensi());
        presensiService.deletePresensi(jadwalKelas);
        List<PresensiModel> newPresensi = new ArrayList<>();
        jadwalKelas.setListPresensi(newPresensi);

        // createListPresensi(jadwalKelas);
        // jadwalKelasDb.save(jadwalKelas);
    }

    @Override
    public void deleteJadwalKelas(JadwalKelasModel jadwalKelas) {
        jadwalKelasDb.delete(jadwalKelas);
    }

    @Override
    public List<JadwalKelasModel> getListJadwalKelasByKelas(KelasModel kelas) {
        return jadwalKelasDb.findAllByKelas(kelas);
    }

    @Override
    public String convertMonthNumberToName(String date) {
        String[] tanggal = date.split("/");
        switch (tanggal[1]){
            case "01":
                tanggal[1] = "Januari";
                break;
            case "02":
                tanggal[1] = "Februari";
                break;
            case "03":
                tanggal[1] = "Maret";
                break;
            case "04":
                tanggal[1] = "April";
                break;
            case "05":
                tanggal[1] = "Mei";
                break;
            case "06":
                tanggal[1] = "Juni";
                break;
            case "07":
                tanggal[1] = "Juli";
                break;
            case "08":
                tanggal[1] = "Agustus";
                break;
            case "09":
                tanggal[1] = "September";
                break;
            case "10":
                tanggal[1] = "Oktober";
                break;
            case "11":
                tanggal[1] = "November";
                break;
            case "12":
                tanggal[1] = "Desember";
                break;
        }
        return String.join(" ",tanggal);
    }

    @Override
    public List<JadwalKelasModel> getJadwalByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByPengajarKelas(pengajar);
        List<JadwalKelasModel> listJadwalKelasNew = new ArrayList<JadwalKelasModel>();
        for (JadwalKelasModel jadwalKelas: listJadwalKelas) {
            if (jadwalKelas.getStartDateClass().toLocalDate().equals(tanggal)){
                listJadwalKelasNew.add(jadwalKelas);
            }
        }
        return listJadwalKelasNew;
    }
    
    @Override
    public List<JadwalKelasModel> getListJadwalKelasByIdPengajar(Integer idPengajar) {
        Optional<PengajarModel> pengajarKelas = pengajarDb.findPengajarModelById(idPengajar);
        if (pengajarKelas.isPresent()){
            return jadwalKelasDb.findAllByPengajarKelas(pengajarKelas.get());
        }
        return new ArrayList<>();
    }

    @Override
    public List<JadwalKelasModel> getListJadwalByUser(UserModel user) {
        List<JadwalKelasModel> listJadwal = new ArrayList<>();
        
        if (user.getRole() == UserRole.SISWA) {
            SiswaModel siswa = siswaService.getSiswaById(user.getId());
            listJadwal = jadwalKelasDb.findAllByKelas(siswaService.getKelasBimbel(siswa));

            
        } else if (user.getRole() == UserRole.PENGAJAR) {
            PengajarModel pengajar = pengajarService.getPengajarById(user.getId());
            listJadwal = jadwalKelasDb.findAllByPengajarKelas(pengajar);

        } 


        List<JadwalKelasModel> res = getListJadwalHariIni(listJadwal);  
        return res;
    }

    public List<JadwalKelasModel> getListJadwalHariIni(List<JadwalKelasModel> listJadwal) {
        List<JadwalKelasModel> listJadwalKelasNew = new ArrayList<JadwalKelasModel>();
        LocalDate tanggal = LocalDate.now();

        for (JadwalKelasModel jadwal : listJadwal) {
            if (jadwal.getStartDateClass().toLocalDate().equals(tanggal)){
                listJadwalKelasNew.add(jadwal);
            }
        }

        return listJadwalKelasNew;
    }
    
    public List<JadwalKelasModel> getListJadwalKelasByTahunAjarAndMonth(TahunAjarModel tahunAjar, Integer month) {
        return jadwalKelasDb.findAllByTahunAjarAndMonth(tahunAjar, month);
    }

    @Override
    public List<JadwalKelasModel> getListJadwalKelasByKelasAndTanggal(LocalDate tanggal, KelasModel kelas) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByKelas(kelas);
        List<JadwalKelasModel> listJadwalKelasTanggal = new ArrayList<>();
        for (JadwalKelasModel jadwalKelas: listJadwalKelas) {
            if (jadwalKelas.getStartDateClass().toLocalDate().equals(tanggal)){
                listJadwalKelasTanggal.add(jadwalKelas);
            }
        } return listJadwalKelasTanggal;
    }
}
