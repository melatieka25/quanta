package com.projectpop.quanta.konsultasi.service;
import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.model.StatusKonsul;
import com.projectpop.quanta.konsultasi.repository.KonsultasiDb;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.siswakonsultasi.service.SiswaKonsultasiService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;

import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.projectpop.quanta.siswajadwalkelas.service.SiswaJadwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.projectpop.quanta.konsultasi.model.StatusKonsul.*;

@Service
@Transactional
public class KonsultasiServiceImpl implements KonsultasiService{
    @Autowired
    KonsultasiDb konsultasiDb;

    @Autowired
    private SiswaService siswaService;

    @Autowired
    private SiswaKonsultasiService siswaKonsultasiService;

    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Autowired
    private PengajarService pengajarService;

    @Autowired
    OrtuService ortuService;

    @Override
    public List<KonsultasiModel> getListKonsultasiHariIni() {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listKonsultasiToday = new ArrayList<>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStartTime().toLocalDate().equals(LocalDate.now())){
                listKonsultasiToday.add(konsultasi);
            }
        }
        return listKonsultasiToday;
    }

    @Override
    public KonsultasiModel getKonsultasi(Integer id) {
        Optional<KonsultasiModel> konsultasi = konsultasiDb.findById(id);
        if (konsultasi.isPresent()) {
            return konsultasi.get();
        } return null;
    }

    @Override
    public List<KonsultasiModel> getListMyKonsultasiPengajar(PengajarModel pengajar) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiPengajar = new ArrayList<KonsultasiModel>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(DITERIMA)) {
                listKonsultasiPengajar.add(konsultasi);
            }
        }
        return listKonsultasiPengajar;

    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByJenjangAndStatus(Jenjang jenjang, StatusKonsul status) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiJenjangStatus = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getJenjangKelas().equals(jenjang) && konsultasi.getStatus().equals(status)) {
                listRequestKonsultasiJenjangStatus.add(konsultasi);
            }
        }

        return listRequestKonsultasiJenjangStatus;
    }

    @Override
    public List<KonsultasiModel> getListMyKonsultasiPengajarAndStatus(PengajarModel pengajar, StatusKonsul status) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiStatus = new ArrayList<KonsultasiModel>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(status)) {
                listKonsultasiStatus.add(konsultasi);
            }
        }
        return listKonsultasiStatus;
    }

    @Override
    public KonsultasiModel createKonsultasi(KonsultasiModel konsultasiModel) {
        return konsultasiDb.save(konsultasiModel);
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiNew = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStartTime().toLocalDate().equals(tanggal)){
                listKonsultasiNew.add(konsultasi);
            }
        }
        return listKonsultasiNew;
    }

    @Override
    public void reloadStatus() {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(PENDING) && konsultasi.getStartTime().minusHours(1).isBefore(LocalDateTime.now())) {
                konsultasi.setStatus(KADALUARSA);
                konsultasiDb.save(konsultasi);
            }
        }

    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByJenjangHariIni(Jenjang jenjang) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiJenjangHariIni = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getJenjangKelas().equals(jenjang) && konsultasi.getStartTime().toLocalDate().equals(LocalDate.now())) {
                listRequestKonsultasiJenjangHariIni.add(konsultasi);
            }
        }

        return listRequestKonsultasiJenjangHariIni;
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiStatus(StatusKonsul status) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiStatus = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(status)) {
                listRequestKonsultasiStatus.add(konsultasi);
            }
        }

        return listRequestKonsultasiStatus;
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByPengajarAndStatusAndTanggal(PengajarModel pengajar, StatusKonsul satus, LocalDate tanggal) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiNew = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStartTime().toLocalDate().equals(tanggal) && konsultasi.getStatus().equals(satus)){
                listKonsultasiNew.add(konsultasi);
            }
        }
        return listKonsultasiNew;
    }

    @Override
    public KonsultasiModel updateKonsultasi(KonsultasiModel konsultasi) {
        konsultasiDb.save(konsultasi);
        return konsultasi;
    }

    @Override
    public boolean getIsSiswaAvailable(SiswaModel siswa, KonsultasiModel konsultasi){
        LocalTime waktuAwal = konsultasi.getStartTime().toLocalTime();
        LocalTime waktuAkhir = konsultasi.getEndTime().toLocalTime();

        List<JadwalKelasModel> listJadwalSiswa = jadwalKelasService.getListJadwalKelasByKelasAndTanggal(konsultasi.getStartTime().toLocalDate(), siswaService.getKelasBimbel(siswa));
        if (listJadwalSiswa.size() != 0) {
            for (JadwalKelasModel jadwalSiswa : listJadwalSiswa) {
                LocalTime notAvailTimeAwal = jadwalSiswa.getStartDateClass().toLocalTime();
                LocalTime notAvailTimeAkhir = jadwalSiswa.getEndDateClass().toLocalTime();
                if (waktuAwal.equals(notAvailTimeAwal)
                        || waktuAkhir.equals(notAvailTimeAkhir)
                        ||(notAvailTimeAwal.isAfter(waktuAwal) && notAvailTimeAwal.isBefore(waktuAkhir))
                        ||(notAvailTimeAkhir.isAfter(waktuAwal) && notAvailTimeAkhir.isBefore(waktuAkhir))
                        ||(waktuAwal.isAfter(notAvailTimeAwal) && waktuAwal.isBefore(notAvailTimeAkhir))
                ) {
                    return false;

                }

            }
        }

        List<SiswaKonsultasiModel> listJadwalKonsultasiSiswa = siswaKonsultasiService.getListKonsultasiBySiswaAndTanggalPendingAndDiterima(siswa, konsultasi.getStartTime().toLocalDate());
        if (null != listJadwalKonsultasiSiswa) {
            for (SiswaKonsultasiModel konsultasiSiswa : listJadwalKonsultasiSiswa) {
                LocalTime notAvailTimeAwal = konsultasiSiswa.getKonsultasi().getStartTime().toLocalTime();
                LocalTime notAvailTimeAkhir = konsultasiSiswa.getKonsultasi().getEndTime().toLocalTime();
                if (waktuAwal.equals(notAvailTimeAwal)
                        || waktuAkhir.equals(notAvailTimeAkhir)
                        ||(notAvailTimeAwal.isAfter(waktuAwal) && notAvailTimeAwal.isBefore(waktuAkhir))
                        ||(notAvailTimeAkhir.isAfter(waktuAwal) && notAvailTimeAkhir.isBefore(waktuAkhir))
                        ||(waktuAwal.isAfter(notAvailTimeAwal) && waktuAwal.isBefore(notAvailTimeAkhir))
                ) {
                    return false;

                }

            }
        }

        return true;
    }

    @Override
    public ArrayList<LocalTime> getNotAvailableWaktuKonsulPengajar(PengajarModel pengajar, LocalDate tanggal){
        List<KonsultasiModel> listTheDayKonsul = getListKonsultasiByPengajarAndStatusAndTanggal(pengajar, DITERIMA, tanggal);
        if (listTheDayKonsul.size() != 0) {
            HashMap<LocalTime, Integer> timeCount = new HashMap<>();
            LocalTime timeCek = listTheDayKonsul.get(0).getStartTime().toLocalTime();
            if (listTheDayKonsul.size()>1) {
                for (int i = 1; i < listTheDayKonsul.size(); i++) {
                    LocalTime thisTime = listTheDayKonsul.get(i).getStartTime().toLocalTime();
                    for (int j = 0; j < listTheDayKonsul.get(i).getDuration(); j++) {
                        if (timeCek.equals(thisTime.plusHours(j))) {
                            if (!timeCount.containsKey(thisTime)) {
                                timeCount.put(thisTime, 1);
                            } else {
                                timeCount.put(thisTime, timeCount.get(thisTime) + 1);
                            }

                        }
                    }
                }
            }

            ArrayList<LocalTime> notAvailableWaktu = new ArrayList<LocalTime>();
            timeCount.forEach((key, value) -> {
                if (value > 3) {
                    notAvailableWaktu.add(key);
                }
            });
            return notAvailableWaktu;
        }
        return null;
    }

    @Override
    public boolean getIsPengajarAvailable(PengajarModel pengajar, KonsultasiModel konsultasi){
        LocalDate tanggal = konsultasi.getStartTime().toLocalDate();
        LocalTime waktuAwal = konsultasi.getStartTime().toLocalTime();
        LocalTime waktuAkhir = konsultasi.getEndTime().toLocalTime();

        ArrayList<LocalTime> listNotAvailableTimePengajar = getNotAvailableWaktuKonsulPengajar(pengajar, tanggal);
        if (null != listNotAvailableTimePengajar) {
            for (LocalTime notAvailTimeAwal : listNotAvailableTimePengajar) {
                LocalTime notAvailTimeAkhir = notAvailTimeAwal.plusHours(1);
                if (waktuAwal.equals(notAvailTimeAwal)
                        || waktuAkhir.equals(notAvailTimeAkhir)
                        ||(notAvailTimeAwal.isAfter(waktuAwal) && notAvailTimeAwal.isBefore(waktuAkhir))
                        ||(notAvailTimeAkhir.isAfter(waktuAwal) && notAvailTimeAkhir.isBefore(waktuAkhir))
                        ||(waktuAwal.isAfter(notAvailTimeAwal) && waktuAwal.isBefore(notAvailTimeAkhir))
                ) {
                    return false;
                }

            }
        }

        List<JadwalKelasModel> listJadwalPengajar = jadwalKelasService.getJadwalByPengajarAndTanggal(pengajar, tanggal);
        if (listJadwalPengajar.size() != 0) {
            for (JadwalKelasModel jadwalPengajar: listJadwalPengajar) {
                LocalTime notAvailTimeAwal = jadwalPengajar.getStartDateClass().toLocalTime();
                LocalTime notAvailTimeAkhir = jadwalPengajar.getEndDateClass().toLocalTime();
                if (waktuAwal.equals(notAvailTimeAwal)
                        || waktuAkhir.equals(notAvailTimeAkhir)
                        ||(notAvailTimeAwal.isAfter(waktuAwal) && notAvailTimeAwal.isBefore(waktuAkhir))
                        ||(notAvailTimeAkhir.isAfter(waktuAwal) && notAvailTimeAkhir.isBefore(waktuAkhir))
                        ||(waktuAwal.isAfter(notAvailTimeAwal) && waktuAwal.isBefore(notAvailTimeAkhir))
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<LocalTime> getListWaktuAwalKonsultasi(LocalDate tanggal) {
        List<LocalTime> listWaktuMulaiKonsul = new ArrayList<>();
        int hari = tanggal.getDayOfWeek().getValue();
        if ((hari >= 1) && (hari <= 5)) {
            for (int i = 14; i < 20; i++) {

                listWaktuMulaiKonsul.add(LocalTime.of(i,00));

            }
        } else if (hari == 6) {
            for (int i = 10; i < 16; i++) {
                listWaktuMulaiKonsul.add(LocalTime.of(i,00));

            }
        }
        return listWaktuMulaiKonsul;
    }

    @Override
    public boolean isInRangeTimeExtend(LocalDateTime waktuAwalKonsul, LocalDateTime waktuAkirKonsulExtend) {
        int hari = waktuAwalKonsul.getDayOfWeek().getValue();

        if ((hari >= 1) && (hari <= 5)) {
            LocalDateTime maxWaktuAkhirExtend = LocalDateTime.of(waktuAwalKonsul.toLocalDate(), LocalTime.parse("20:00"));
            if (waktuAkirKonsulExtend.isAfter(maxWaktuAkhirExtend)){
                return false;
            }
        } else if (hari == 6) {
            LocalDateTime maxWaktuAkhirExtend = LocalDateTime.of(waktuAwalKonsul.toLocalDate(), LocalTime.parse("16:00"));
            if (waktuAkirKonsulExtend.isAfter(maxWaktuAkhirExtend)){
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByUser(UserModel user) {
        List<KonsultasiModel> listKonsul = new ArrayList<>();
        
        if (user.getRole() == UserRole.SISWA) {
            SiswaModel siswa = siswaService.getSiswaById(user.getId());
            List<SiswaKonsultasiModel> listSiswaKonsul = siswaKonsultasiService.getListKonsultasiBySiswa(siswa);

            for (SiswaKonsultasiModel siswaKonsul : listSiswaKonsul) {
                listKonsul.add(siswaKonsul.getKonsultasi());
            }

            
        } else if (user.getRole() == UserRole.PENGAJAR) {
            PengajarModel pengajar = pengajarService.getPengajarById(user.getId());
            listKonsul = konsultasiDb.findAllByPengajarKonsul(pengajar);

        }  else if (user.getRole() == UserRole.ORTU) {
            OrtuModel ortu = ortuService.getOrtuById(user.getId());
            List<SiswaModel> listAnak= ortu.getListAnak();
            SiswaModel anakSelected = listAnak.get(0);

            if (!anakSelected.getIsActive()){    
                for (SiswaModel anak : listAnak) {
                    if (anak.getIsActive()) {
                        anakSelected = anak;
                        break;
                    }
                }
            }

            List<SiswaKonsultasiModel> listSiswaKonsul = siswaKonsultasiService.getListKonsultasiBySiswa(anakSelected);

            for (SiswaKonsultasiModel siswaKonsul : listSiswaKonsul) {
                listKonsul.add(siswaKonsul.getKonsultasi());
            }
        }

        
        return getListKonsulHariIni(listKonsul);
    }

    public List<KonsultasiModel> getListKonsulHariIni(List<KonsultasiModel> listKonsul) {
        List<KonsultasiModel> res = new ArrayList<>();
        LocalDate tanggal = LocalDate.now();
        for (KonsultasiModel konsul : listKonsul) {
            if (konsul.getStartTime().toLocalDate().equals(tanggal) && konsul.getStatus() == StatusKonsul.DITERIMA) {
                res.add(konsul);
            }
        }

        return res;
    }
}
