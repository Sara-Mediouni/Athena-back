package com.example.demo.API.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.API.dto.CAglobalDTO;
import com.example.demo.API.service.CAGlobalService;
@RestController
@RequestMapping("/api/caglobal")
public class CAglobalController {
    private final CAGlobalService caGlobalService;
    public CAglobalController(CAGlobalService caGlobalService) {
        this.caGlobalService = caGlobalService;
    }
    @GetMapping("/chiffre-affaire")
public List<CAglobalDTO> getCaGlobal(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
    @RequestParam String mode,
    @RequestParam String InclureBLs,
    @RequestParam Long id
) {
    return caGlobalService.getChiffreAffaireGlobal(dateDebut,id, dateFin, mode,InclureBLs);
}
    @GetMapping("/chiffre-periode")
public List<CAglobalDTO> getCaPeriode(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
    @RequestParam String mode,
    @RequestParam String InclureBLs,
    @RequestParam String groupBy,
    @RequestParam Long id
) {
    return caGlobalService.getChiffreAffairePeriode(dateDebut, dateFin, mode,id,InclureBLs,groupBy);
}
}
