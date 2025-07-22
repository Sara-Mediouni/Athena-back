package com.example.demo.API.service;

import com.example.demo.API.Entity.DataApiEntity;
import com.example.demo.API.dto.CAglobalDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CAGlobalService {
    @Autowired
    private DataApiClient dataApiClient;
   
    private static class MonthSummary {
        LocalDate minDate;
        LocalDate maxDate;
        double total;
        double totalht;

        void update(LocalDate date, double ttc,double ht) {
            if (minDate == null || date.isBefore(minDate)) {
                minDate = date;
            }
            if (maxDate == null || date.isAfter(maxDate)) {
                maxDate = date;
            }
            total += ttc;
            totalht += ht;
        }
    }

    // Méthode principale
    public List<CAglobalDTO> getChiffreAffaireGlobal(LocalDate dateDebut, LocalDate dateFin, String modeDate, String InclureBLs ) {
      
        List<DataApiEntity> lignes = dataApiClient.fetchData(dateDebut, dateFin,InclureBLs,modeDate);
        System.out.println(lignes.size() + " lignes récupérées entre " + dateDebut + " et " + dateFin);
      //  Map<String, MonthSummary> map = new TreeMap<>();
        Double totalttc=0.0;
        Double totalht=0.0;
        Integer document=0;
        for (DataApiEntity ligne : lignes) {
            

            LocalDate date = "dateBL".equalsIgnoreCase(modeDate)
                    ? ligne.getDateBL()
                    : ligne.getDate();
             System.out.println(date);
             System.out.println(ligne.getTtc());
            if (date == null || date.isBefore(dateDebut) || date.isAfter(dateFin)) {
                continue;
            }

            if (ligne.getTtc() == null || ligne.getTtc() == 0.0) {
                continue;
            }
             if (ligne.getHt() == null || ligne.getHt() == 0.0) {
                continue;
            }
            document+=1;
            totalttc+=ligne.getTtc();
            totalht+=ligne.getHt();
            System.out.println(document);
            // Clé de regroupement : par mois et année
          //  String key = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
           // System.out.println(date.getYear() + "-" + String.format("%02d", date.getMonthValue()));
          //  System.out.println("Traitement de la ligne : " + ligne.getId() + ", Date : " + date + ", TTC : " + ligne.getTtc());
          //  System.out.println("Clé de regroupement : " + key);
          //  map.putIfAbsent(key, new MonthSummary());
          //  map.get(key).update(date, ligne.getTtc());
            
        }
     
 CAglobalDTO result = new CAglobalDTO(dateDebut, dateFin, totalttc,totalht,document);
    return Collections.singletonList(result);
        // Transformation en DTO pour l'envoi au front
       // return map.values().stream()
           //     .map(summary -> new CAglobalDTO(dateDebut, dateFin, total))
            //    .collect(Collectors.toList());
    }

 }
