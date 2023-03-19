package com.projectpop.quanta.konsultasi.restcontroller;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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


    @GetMapping("/get-pengajar-mapel/{idMapel}")
    private List<PengajarModel> getListPengajarByMapel(@PathVariable("idMapel") Integer idMapel) {
        MataPelajaranModel mataPelajaran = mataPelajaranService.getMapelById(idMapel);
        List<PengajarMapelModel> listPMapel = pengajarMapelService.getListPengajarByMapel(mataPelajaran);

        // get value of mapel
        List<PengajarModel> listPengajar = new ArrayList<>();
        for (PengajarMapelModel pMapel : listPMapel) {
            listPengajar.add(pMapel.getPengajar());
        }
        return listPengajar;
    }


    @GetMapping("/get-waktu/{idPengajar}/{tanggal}")
    private List<LocalTime> getAvailableWaktuAwal(Authentication authentication, @PathVariable("idPengajar") Integer idPengajar, @PathVariable("tanggal") String tanggal){

        SiswaModel siswa = siswaService.findSiswaByEmail(authentication.getName());
        PengajarModel pengajar = pengajarService.getPengajarById(idPengajar);

        List<SiswaJadwalModel> listSiswaJadwal = siswaJadwalService.getListSiswaJadwalBySiswaAndDate(siswa, LocalDate.parse(tanggal));
        List<ArrayList<LocalTime>> listWaktuTidakTersedia = new ArrayList<ArrayList<LocalTime>>();
        ArrayList<LocalTime> waktuTidakTersedia = new ArrayList<LocalTime>(2);

//        ambil jadwal siswa
        if (null != listSiswaJadwal) {
            for (SiswaJadwalModel siswaJadwal : listSiswaJadwal) {
                LocalTime waktuMulaiSiswa = siswaJadwal.getJadwalKelas().getStartDateClass().toLocalTime();
                LocalTime waktuSelesaiSiswa = siswaJadwal.getJadwalKelas().getEndDateClass().toLocalTime();
                waktuTidakTersedia.set(0, waktuMulaiSiswa);
                waktuTidakTersedia.set(1, waktuSelesaiSiswa);
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }

//        ambil jadwal pengajar
        List<JadwalKelasModel> listJadwalPengajar = jadwalKelasService.getJadwalByPengajarAndTanggal(pengajar, LocalDate.parse(tanggal));
        if (null != listJadwalPengajar) {
            for (JadwalKelasModel jadwalPengajar: listJadwalPengajar) {
                LocalTime waktuMulaiPengajar = jadwalPengajar.getStartDateClass().toLocalTime();
                LocalTime waktuSelesaiPengajar = jadwalPengajar.getEndDateClass().toLocalTime();
                waktuTidakTersedia.set(0, waktuMulaiPengajar);
                waktuTidakTersedia.set(1, waktuSelesaiPengajar);
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }
//        ambil jadwal konsul harian
        List<LocalTime> listWaktuAwalKonsultasi = getListWaktuAwalKonsultasi(LocalDate.parse(tanggal));

        //                pengecekan bentrok
        for (LocalTime waktuAwalKonsultasi: listWaktuAwalKonsultasi) {
            LocalTime waktuAkhirKonsultasi = waktuAwalKonsultasi.plusHours(1);
            for (ArrayList<LocalTime> startAndEndTime: listWaktuTidakTersedia
            ) {
                LocalTime waktuAwalNotAvailable = startAndEndTime.get(0);
                LocalTime waktuAkhirNotAvailable = startAndEndTime.get(1);

                if ((waktuAwalKonsultasi.isAfter(waktuAwalNotAvailable) && waktuAwalKonsultasi.isBefore(waktuAkhirNotAvailable))
                        || (waktuAkhirKonsultasi.isAfter(waktuAwalNotAvailable) && waktuAkhirKonsultasi.isBefore(waktuAkhirNotAvailable))
                        || (waktuAwalNotAvailable.isAfter(waktuAwalKonsultasi) && waktuAwalNotAvailable.isBefore(waktuAkhirKonsultasi))
                        || (waktuAkhirNotAvailable.isAfter(waktuAwalKonsultasi) && waktuAkhirNotAvailable.isBefore(waktuAkhirKonsultasi) ) ){

                    listWaktuAwalKonsultasi.remove(waktuAwalKonsultasi);
                    break;
                }

            }
        }

        return listWaktuAwalKonsultasi;

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

}
