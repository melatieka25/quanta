package com.projectpop.quanta.presensi.controller;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.service.PengajarServiceImpl;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.model.PresensiStatus;
import com.projectpop.quanta.presensi.service.PresensiServiceImpl;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaServiceImpl;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserServiceImpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/presensi")
public class PresensiController {
    @Qualifier("jadwalKelasServiceImpl")
    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Qualifier("presensiServiceImpl")
    @Autowired
    private PresensiServiceImpl presensiService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserServiceImpl userService;

    @Qualifier("pengajarServiceImpl")
    @Autowired
    private PengajarServiceImpl pengajarService;

    @GetMapping("")
    public String viewAllJadwalPengajarHariIni(Model model, Principal principal) throws ParseException {
        String tahunAjaran;
        int currentYear = LocalDate.now().getYear();
        int yearBefore = LocalDate.now().minusYears(1).getYear();
        int yearAfter = LocalDate.now().plusYears(1).getYear();
        if (9<=LocalDate.now().getMonthValue() || LocalDate.now().getMonthValue()==1){
            tahunAjaran = currentYear + "/" + yearAfter;
        }
        else{
            tahunAjaran = yearBefore + "/" +currentYear;
        }
        Map<JadwalKelasModel,String> jadwalKelasHariIniMap = new HashMap<>();
        Map<JadwalKelasModel,String[]> allJadwalKelasMap = new HashMap<>();
        List<JadwalKelasModel> listAllJadwalKelas = jadwalKelasService.getListJadwalKelas();
        List<JadwalKelasModel> listJadwalKelasHariIni = new ArrayList<>();

        var userModel = userService.getUserByEmail(principal.getName());
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelasByIdPengajar(userModel.getId());
        for (JadwalKelasModel jadwal : listJadwalKelas){
            String startDate = localDateTimeToDateWithSlash(jadwal.getStartDateClass());
            if (startDate.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))){
                listJadwalKelasHariIni.add(jadwal);
            }
        }
        for(int i = 0; i < listAllJadwalKelas.size(); i++){
            for(int j = 0; j <listJadwalKelasHariIni.size(); j++){
                if(listAllJadwalKelas.get(i).equals(listJadwalKelasHariIni.get(j))) {
                    listAllJadwalKelas.get(i).setIsiPresensi(true);
                }
                else{
                    listAllJadwalKelas.get(i).setIsiPresensi(false);
                }
            }
        }
        for (JadwalKelasModel jadwalKelasModel : listJadwalKelasHariIni) {
            jadwalKelasModel.setIsiPresensi(true);
        }

        for (JadwalKelasModel jadwalKelasHariIni : listJadwalKelasHariIni){
            String startDate = localDateTimeToDateWithSlash(jadwalKelasHariIni.getStartDateClass());
            String startClock = localDateTimeToTimeWithSlash(jadwalKelasHariIni.getStartDateClass());
            String endClock = localDateTimeToTimeWithSlash(jadwalKelasHariIni.getEndDateClass());
            String time = String.format("%s - %s",startClock, endClock);
            model.addAttribute("startClock", startClock);
            jadwalKelasHariIniMap.put(jadwalKelasHariIni,time);
            model.addAttribute("startDate", startDate);
        }
        for (JadwalKelasModel jadwal : listAllJadwalKelas){
            String startDate = localDateTimeToDateWithSlash(jadwal.getStartDateClass());
            String startClock = localDateTimeToTimeWithSlash(jadwal.getStartDateClass());
            String endClock = localDateTimeToTimeWithSlash(jadwal.getEndDateClass());
            String time = String.format("%s - %s",startClock, endClock);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            //convert String to LocalDate
            LocalDate localDate = LocalDate.parse(startDate, formatter);
            if (localDate.isBefore(LocalDate.now())){
                allJadwalKelasMap.put(jadwal, new String[]{time, startDate});
            }
        }
        model.addAttribute("tahunAjaran", tahunAjaran);
        model.addAttribute("tanggal", LocalDate.now());
        model.addAttribute("jadwalKelasHariIniMap", jadwalKelasHariIniMap);
        model.addAttribute("allJadwalKelasMap", allJadwalKelasMap);
        return "presensi/landing-page";
    }
    public static String localDateTimeToDateWithSlash(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDateTime);
    }
    public static String localDateTimeToTimeWithSlash(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("hh:mm:ss").format(localDateTime);
    }
    @GetMapping(value = "/read/{idJadwalKelas}")
    public String viewDetailPresensi(@PathVariable("idJadwalKelas") Integer idJadwalKelas, Model model){
        String tahunAjaran;
        int currentYear = LocalDate.now().getYear();
        int yearBefore = LocalDate.now().minusYears(1).getYear();
        int yearAfter = LocalDate.now().plusYears(1).getYear();
        if (9<=LocalDate.now().getMonthValue() || LocalDate.now().getMonthValue()==1){
            tahunAjaran = currentYear + "/" + yearAfter;
        }
        else{
            tahunAjaran = yearBefore + "/" +currentYear;
        }
        JadwalKelasModel jadwalKelasModel = jadwalKelasService.getJadwalKelasById(idJadwalKelas);
        model.addAttribute("jadwalKelasModel", jadwalKelasModel);
        model.addAttribute("idJadwalKelas", idJadwalKelas);
        model.addAttribute("tahunAjaran", tahunAjaran);
        model.addAttribute("tanggal", LocalDate.now());
        model.addAttribute("kelas", jadwalKelasModel.getKelas());
        model.addAttribute("presensiModelList", jadwalKelasModel.getListPresensi());
        System.out.println(jadwalKelasModel.getListPresensi());
        return "presensi/read-presensi-kelas";
    }
    @GetMapping("/{idJadwalKelas}")
    public String updatePresensiKelasFormPage(@PathVariable String idJadwalKelas, Model model){
        String tahunAjaran;
        JadwalKelasModel jadwalKelasModel = jadwalKelasService.getJadwalKelasById(Integer.parseInt(idJadwalKelas));
        List<PresensiModel> presensiModelList = jadwalKelasModel.getListPresensi();
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userModel = userService.getUserByEmail(userDetails.getUsername());
        if (!userModel.getEmail().equals(jadwalKelasModel.getPengajarKelas().getEmail())){
            return "redirect:/presensi/read/"+jadwalKelasModel.getId();
        }
        if(!localDateTimeToDateWithSlash(jadwalKelasModel.getStartDateClass()).equals(localDateTimeToDateWithSlash(LocalDateTime.now()))){
            return "redirect:/presensi/read/"+jadwalKelasModel.getId();
        }
        int currentYear = LocalDate.now().getYear();
        int yearBefore = LocalDate.now().minusYears(1).getYear();
        int yearAfter = LocalDate.now().plusYears(1).getYear();
        if (9<=LocalDate.now().getMonthValue() || LocalDate.now().getMonthValue()==1){
            tahunAjaran = currentYear + "/" + yearAfter;
        }
        else{
            tahunAjaran = yearBefore + "/" +currentYear;
        }
        model.addAttribute("listPresensiStatus", PresensiStatus.values());
        model.addAttribute("tahunAjaran", tahunAjaran);
        model.addAttribute("tanggal", LocalDate.now());
        model.addAttribute("kelas", jadwalKelasModel.getKelas());
        model.addAttribute("presensiModelList", presensiModelList);
        model.addAttribute("presensiFilled", false);
        model.addAttribute("jadwalKelasModel", jadwalKelasModel);
        return "presensi/presensi-kelas";
    }
    @PostMapping(value = "/read/{idJadwalKelas}", params = {"save"})
    public String updatePresensiKelasSubmitPage(@PathVariable String idJadwalKelas, Model model,  @ModelAttribute JadwalKelasModel jadwalKelasModel){
        String tahunAjaran;
        int currentYear = LocalDate.now().getYear();
        JadwalKelasModel jadwalKelasModelUpdated = jadwalKelasService.getJadwalKelasById(Integer.parseInt(idJadwalKelas));
        List<PresensiModel> presensiUpdatedList = jadwalKelasModelUpdated.getListPresensi();
        for (int i = 0; i<presensiUpdatedList.size(); i++){
            presensiUpdatedList.get(i).setStatus(jadwalKelasModel.getListPresensi().get(i).getStatus());
            presensiService.updatePresensi(presensiUpdatedList.get(i));
        }
        int yearBefore = LocalDate.now().minusYears(1).getYear();
        int yearAfter = LocalDate.now().plusYears(1).getYear();
        if (9<=LocalDate.now().getMonthValue() || LocalDate.now().getMonthValue()==1){
            tahunAjaran = currentYear + "/" + yearAfter;
        }
        else{
            tahunAjaran = yearBefore + "/" +currentYear;
        }
        for (PresensiModel presensiModel : jadwalKelasModelUpdated.getListPresensi()){
            presensiService.updatePresensi(presensiModel);
        }
        model.addAttribute("idJadwalKelas", idJadwalKelas);
        model.addAttribute("listPresensiStatus", PresensiStatus.values());
        model.addAttribute("tahunAjaran", tahunAjaran);
        model.addAttribute("tanggal", LocalDate.now());
        model.addAttribute("kelas", jadwalKelasModelUpdated.getKelas());
        model.addAttribute("presensiModelList", jadwalKelasModelUpdated.getListPresensi());
        return "redirect:/presensi/read/"+jadwalKelasModelUpdated.getId();
    }

}