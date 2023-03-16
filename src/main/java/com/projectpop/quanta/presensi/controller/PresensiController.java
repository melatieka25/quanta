package com.projectpop.quanta.presensi.controller;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.presensi.service.PresensiServiceImpl;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserServiceImpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("")
    public String viewAllJadwalPengajarHariIni(Model model, Principal principal) throws ParseException {
        Map<JadwalKelasModel,String> jadwalKelasHariIniMap = new HashMap<>();
        Map<JadwalKelasModel,String[]> allJadwalKelasMap = new HashMap<>();
        List<JadwalKelasModel> listAllJadwalKelas = jadwalKelasService.getAllListJadwalKelas();
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
        var userModel = userService.getUserByEmail(principal.getName());
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelasByIdPengajar(userModel.getId());
        for (JadwalKelasModel jadwal : listJadwalKelas){
            String startDate = localDateTimeToDateWithSlash(jadwal.getStartDateClass());
            String startClock = localDateTimeToTimeWithSlash(jadwal.getStartDateClass());
            String endClock = localDateTimeToTimeWithSlash(jadwal.getEndDateClass());
            String time = String.format("%s - %s",startClock, endClock);
            model.addAttribute("startClock", startClock);
            if (startDate.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))){
                jadwalKelasHariIniMap.put(jadwal,time);
                model.addAttribute("startDate", startDate);
            }
        }
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

}
