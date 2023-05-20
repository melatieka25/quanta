package com.projectpop.quanta.jadwalkelas.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JadwalKelasDTO {
    String title;
    LocalDateTime start;
    LocalDateTime end;
    String url;
}
