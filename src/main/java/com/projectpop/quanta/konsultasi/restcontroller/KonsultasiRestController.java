package com.projectpop.quanta.konsultasi.restcontroller;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import static com.projectpop.quanta.konsultasi.model.StatusKonsul.*;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MataPelajaranService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.projectpop.quanta.siswajadwalkelas.service.SiswaJadwalService;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.siswakonsultasi.service.SiswaKonsultasiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/konsultasi")
public class KonsultasiRestController {
    @Autowired
    private PengajarMapelService pengajarMapelService;

    @Autowired
    private MataPelajaranService mataPelajaranService;

    @Autowired
    private PengajarService pengajarService;

    @Autowired
    private SiswaService siswaService;

    @Autowired
    private SiswaJadwalService siswaJadwalService;

    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Autowired
    private SiswaKonsultasiService siswaKonsultasiService;

    @Autowired
    private KonsultasiService konsultasiService;


    @GetMapping("/get-pengajar-mapel/{idMapel}")
    private List<PengajarModel> getListPengajarByMapel(@PathVariable("idMapel") Integer idMapel) {
        MataPelajaranModel mataPelajaran = mataPelajaranService.getMapelById(idMapel);
        List<PengajarMapelModel> listPMapel = pengajarMapelService.getListPengajarByMapel(mataPelajaran);

        // get value of mapel
        List<PengajarModel> listPengajar = new ArrayList<>();
        if (listPMapel.size() != 0) {
            for (PengajarMapelModel pMapel : listPMapel) {
                listPengajar.add(pMapel.getPengajar());
            }
        }
        return listPengajar;
    }


    @GetMapping("/get-waktu/{idPengajar}/{tanggal}")
    private List<LocalTime> getAvailableWaktuAwal(Principal principal, @PathVariable("idPengajar") Integer idPengajar, @PathVariable("tanggal") String tanggal){

        SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
        PengajarModel pengajar = pengajarService.getPengajarById(idPengajar);

        List<SiswaJadwalModel> listSiswaJadwal = siswaJadwalService.getListSiswaJadwalBySiswaAndDate(siswa, LocalDate.parse(tanggal));
        List<ArrayList<LocalTime>> listWaktuTidakTersedia = new ArrayList<ArrayList<LocalTime>>();

//        ambil jadwal siswa
        if (listSiswaJadwal.size() != 0) {
            for (SiswaJadwalModel siswaJadwal : listSiswaJadwal) {
                LocalTime waktuMulaiSiswa = siswaJadwal.getJadwalKelas().getStartDateClass().toLocalTime();
                LocalTime waktuSelesaiSiswa = siswaJadwal.getJadwalKelas().getEndDateClass().toLocalTime();
                ArrayList<LocalTime> waktuTidakTersedia = new ArrayList<LocalTime>(2);
                waktuTidakTersedia.add(waktuMulaiSiswa);
                waktuTidakTersedia.add(waktuSelesaiSiswa);
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }

//        ambil jadwal pengajar
        List<JadwalKelasModel> listJadwalPengajar = jadwalKelasService.getJadwalByPengajarAndTanggal(pengajar, LocalDate.parse(tanggal));
        if (listJadwalPengajar.size() != 0) {
            for (JadwalKelasModel jadwalPengajar: listJadwalPengajar) {
                LocalTime waktuMulaiPengajar = jadwalPengajar.getStartDateClass().toLocalTime();
                LocalTime waktuSelesaiPengajar = jadwalPengajar.getEndDateClass().toLocalTime();
                ArrayList<LocalTime> waktuTidakTersedia = new ArrayList<LocalTime>(2);
                waktuTidakTersedia.add(waktuMulaiPengajar);
                waktuTidakTersedia.add(waktuSelesaiPengajar);
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }
        
//        ambil jadwal konsul siswa
        List<SiswaKonsultasiModel> listJadwalKonsultasiSiswa = siswaKonsultasiService.getListKonsultasiBySiswaAndTanggal(siswa, LocalDate.parse(tanggal));
        if (listJadwalKonsultasiSiswa.size() != 0) {
            for (SiswaKonsultasiModel siswaKonsul: listJadwalKonsultasiSiswa) {
                LocalTime waktuMulaiKonsul = siswaKonsul.getKonsultasi().getStartTime().toLocalTime();
                LocalTime waktuSelesaiKonsul = siswaKonsul.getKonsultasi().getEndTime().toLocalTime();
                ArrayList<LocalTime> waktuTidakTersedia = new ArrayList<LocalTime>(2);
                waktuTidakTersedia.add(waktuMulaiKonsul);
                waktuTidakTersedia.add(waktuSelesaiKonsul);
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }

//      ambil jadwal konsultasi pengajar -> dengan jumlah konsultasi > 3 dalam 1 waktu
        ArrayList<LocalTime> listNotAvailableTimePengajar = getNotAvailableWaktuKonsulPengajar(pengajar, LocalDate.parse(tanggal));
        if (null != listNotAvailableTimePengajar) {
            for (LocalTime notAvailTimeAwal : listNotAvailableTimePengajar) {
                ArrayList<LocalTime> waktuTidakTersedia = new ArrayList<LocalTime>(2);
                waktuTidakTersedia.add(notAvailTimeAwal);
                waktuTidakTersedia.add(notAvailTimeAwal.plusHours(1));
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }


//        ambil jadwal konsul harian
        List<LocalTime> listWaktuAwalKonsultasi = getListWaktuAwalKonsultasi(LocalDate.parse(tanggal));
        List<LocalTime> listWaktuAwalKonsultasiTersedia = new ArrayList<LocalTime>();
        listWaktuAwalKonsultasiTersedia.addAll(listWaktuAwalKonsultasi);

        //                pengecekan bentrok
        for (LocalTime waktuAwalKonsultasi: listWaktuAwalKonsultasi) {
            LocalTime waktuAkhirKonsultasi = waktuAwalKonsultasi.plusHours(1);
            if (listWaktuTidakTersedia.size() != 0) {
                for (ArrayList<LocalTime> startAndEndTime : listWaktuTidakTersedia
                ) {
                    LocalTime waktuAwalNotAvailable = startAndEndTime.get(0);
                    LocalTime waktuAkhirNotAvailable = startAndEndTime.get(1);

                    if (waktuAwalKonsultasi.equals(waktuAwalNotAvailable)
                            || waktuAkhirKonsultasi.equals(waktuAkhirNotAvailable)
                            ||(waktuAwalNotAvailable.isAfter(waktuAwalKonsultasi) && waktuAwalNotAvailable.isBefore(waktuAkhirKonsultasi))
                            ||(waktuAkhirNotAvailable.isAfter(waktuAwalKonsultasi) && waktuAkhirNotAvailable.isBefore(waktuAkhirKonsultasi))
                            ||(waktuAwalKonsultasi.isAfter(waktuAwalNotAvailable) && waktuAwalKonsultasi.isBefore(waktuAkhirNotAvailable))
                    ) {
                        listWaktuAwalKonsultasiTersedia.remove(waktuAwalKonsultasi);
                        break;
                    }

                }
            }
        }
        List<LocalTime> listWaktuAwalKonsultasiTersediaToday = new ArrayList<LocalTime>();
        if(LocalDate.parse(tanggal).equals(LocalDate.now())) {
            for (LocalTime availabletime: listWaktuAwalKonsultasiTersedia) {
                if (availabletime.isAfter(LocalTime.now().plusHours(3))){
                    listWaktuAwalKonsultasiTersediaToday.add(availabletime);
                }
            }
            return listWaktuAwalKonsultasiTersediaToday;
        }
        return listWaktuAwalKonsultasiTersedia;

    }

    @GetMapping("/get-waktu-default")
    private List<LocalTime> getListWaktuAwalKonsultasi(LocalDate tanggal){
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

    private ArrayList<LocalTime> getNotAvailableWaktuKonsulPengajar(PengajarModel pengajar, LocalDate tanggal){
        List<KonsultasiModel> listTheDayKonsul = konsultasiService.getListKonsultasiByPengajarAndStatusAndTanggal(pengajar, DITERIMA, tanggal);
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
            }); return notAvailableWaktu;
        }
        return null;
    }

}
