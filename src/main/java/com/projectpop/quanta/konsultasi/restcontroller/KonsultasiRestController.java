package com.projectpop.quanta.konsultasi.restcontroller;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.siswajadwalkelas.service.SiswaJadwalService;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.siswakonsultasi.service.SiswaKonsultasiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
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
    private MapelService mapelService;

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
        MataPelajaranModel mataPelajaran = mapelService.getMapelById(idMapel);
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

        List<JadwalKelasModel> listJadwalSiswa = jadwalKelasService.getListJadwalKelasByKelasAndTanggal(LocalDate.parse(tanggal), siswaService.getKelasBimbel(siswa));
        List<ArrayList<LocalTime>> listWaktuTidakTersedia = new ArrayList<ArrayList<LocalTime>>();

//        ambil jadwal siswa
        if (listJadwalSiswa.size() != 0) {
            for (JadwalKelasModel jadwalSiswa : listJadwalSiswa) {
                LocalTime waktuMulaiSiswa = jadwalSiswa.getStartDateClass().toLocalTime();
                LocalTime waktuSelesaiSiswa = jadwalSiswa.getEndDateClass().toLocalTime();
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
        List<SiswaKonsultasiModel> listJadwalKonsultasiSiswa = siswaKonsultasiService.getListKonsultasiBySiswaAndTanggalPendingAndDiterima(siswa, LocalDate.parse(tanggal));
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
        ArrayList<LocalTime> listNotAvailableTimePengajar = konsultasiService.getNotAvailableWaktuKonsulPengajar(pengajar, LocalDate.parse(tanggal));
        if (null != listNotAvailableTimePengajar) {
            for (LocalTime notAvailTimeAwal : listNotAvailableTimePengajar) {
                ArrayList<LocalTime> waktuTidakTersedia = new ArrayList<LocalTime>(2);
                waktuTidakTersedia.add(notAvailTimeAwal);
                waktuTidakTersedia.add(notAvailTimeAwal.plusHours(1));
                listWaktuTidakTersedia.add(waktuTidakTersedia);
            }
        }


//        ambil jadwal konsul harian
        List<LocalTime> listWaktuAwalKonsultasi = konsultasiService.getListWaktuAwalKonsultasi(LocalDate.parse(tanggal));
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


}
