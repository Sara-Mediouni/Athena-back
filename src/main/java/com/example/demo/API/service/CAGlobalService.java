package com.example.demo.API.service;

import com.example.demo.API.Entity.DataApiEntity;
import com.example.demo.API.dto.CAglobalDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CAGlobalService {
    @Autowired
    private DataApiClient dataApiClient;
   private static final DecimalFormat df = new DecimalFormat("0.00");
    private static class MonthSummary {
        LocalDate minDate;
        LocalDate maxDate;
        double total;
        double totalht;
        double totalqte;
        void update(LocalDate date, double ttc,double ht, double qte) {
            if (minDate == null || date.isBefore(minDate)) {
                minDate = date;
            }
            if (maxDate == null || date.isAfter(maxDate)) {
                maxDate = date;
            }
            total += ttc;
            totalht += ht;
            totalqte+=qte;
        }
    }
    private String generateLabel(String key, String groupBy, MonthSummary summary) {
    switch (groupBy.toLowerCase()) {
        case "jour":
            return summary.minDate.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)); 
        case "semaine":
            return String.format(
                "Semaine %s (%s au %s)",
                key.substring(key.indexOf('S') + 1), 
                summary.minDate.format(DateTimeFormatter.ofPattern("dd MMM", Locale.FRENCH)),
                summary.maxDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH))
            );
            case "commercial":
            return key; 
            case "client":
            return key; 
            case "article":
            return  key;
            case "depot":
            return  key; 
        case "mois":
        default:
            return summary.minDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)); 
    }
}

 public List<CAglobalDTO> getChiffreAffairePeriode(LocalDate dateDebut, LocalDate dateFin, String modeDate,Long id, String inclureBLs, String groupBy) {
    List<DataApiEntity> lignes = dataApiClient.fetchData(dateDebut, dateFin, inclureBLs, modeDate,id);
    
    System.out.println(lignes.size() + " lignes récupérées entre " + dateDebut + " et " + dateFin);

    Map<String, MonthSummary> map = new TreeMap<>();
    Integer document = 0;

    WeekFields weekFields = WeekFields.ISO;

    for (DataApiEntity ligne : lignes) {
        LocalDate date = "dateBL".equalsIgnoreCase(modeDate) ? ligne.getDateBL() : ligne.getDate();

        if (date == null || date.isBefore(dateDebut) || date.isAfter(dateFin)) continue;
        if (ligne.getTtc() == null || ligne.getTtc() == 0.0) continue;
        if (ligne.getHt() == null || ligne.getHt() == 0.0) continue;
         if (ligne.getQte() == null || ligne.getQte() == 0.0) {
                continue;
            }

        String key;
        switch (groupBy.toLowerCase()) {
            case "jour":
                key = date.toString();
                break;
            case "semaine":
                int week = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.get(weekFields.weekBasedYear());
                key = String.format("%d-S%02d", year, week); 
                break;
            case "commercial":
                if (ligne.getCoNom() != null) {
                key =ligne.getCoNom();
                break;
            }
            case "article":
                if (ligne.getDesignation() != null) {
                key =ligne.getDesignation();
                break;
            }
            case "client":
                if (ligne.getCtIntitule() != null) {
                key =ligne.getCtIntitule();
                
            }
                     
    else {
        continue; 
    }           break;
    
                case "depot":
                if (ligne.getDepot() != null) {
                key = ligne.getDepot();
        
    } else {
        continue; 
    }
    
            break;
            case "mois":
            
            default:
                key = date.getYear() + "-" + String.format("%02d", date.getMonthValue()); 
                break;
        }

        map.putIfAbsent(key, new MonthSummary());
        map.get(key).update(date, ligne.getTtc(), ligne.getHt(),ligne.getQte());
    }

  return map.entrySet().stream()
    .map(entry -> {
        String key = entry.getKey();
        MonthSummary summary = entry.getValue();
        String label = generateLabel(key, groupBy, summary);

        return new CAglobalDTO(
            summary.minDate,
            summary.maxDate,
            summary.total,
            summary.totalht,
            summary.totalqte,
            document,
            label 
        );
    })
    .collect(Collectors.toList());

}

   
    public List<CAglobalDTO> getChiffreAffaireGlobal(LocalDate dateDebut,Long id, LocalDate dateFin, String modeDate, String InclureBLs ) {
      
        List<DataApiEntity> lignes = dataApiClient.fetchData(dateDebut, dateFin,InclureBLs,modeDate,id);
      
        Double totalttc=0.0;
        Double totalht=0.0;
        Integer document=0;
        Double totalqte=0.0;
        for (DataApiEntity ligne : lignes) {
            

            LocalDate date = "dateBL".equalsIgnoreCase(modeDate)
                    ? ligne.getDateBL()
                    : ligne.getDate();
            
            if (date == null || date.isBefore(dateDebut) || date.isAfter(dateFin)) {
                continue;
            }

            if (ligne.getTtc() == null || ligne.getTtc() == 0.0) {
                continue;
            }
             if (ligne.getHt() == null || ligne.getHt() == 0.0) {
                continue;
            }
            if (ligne.getQte() == null || ligne.getQte() == 0.0) {
                continue;
            }
            document+=1;
            totalttc+=ligne.getTtc();
            totalht+=ligne.getHt();
            totalqte+=ligne.getQte();
            System.out.println(totalqte);
           
          //  String key = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
           // System.out.println(date.getYear() + "-" + String.format("%02d", date.getMonthValue()));
          //  System.out.println("Traitement de la ligne : " + ligne.getId() + ", Date : " + date + ", TTC : " + ligne.getTtc());
          //  System.out.println("Clé de regroupement : " + key);
          //  map.putIfAbsent(key, new MonthSummary());
          //  map.get(key).update(date, ligne.getTtc());
            
        }
     
 CAglobalDTO result = new CAglobalDTO(dateDebut, dateFin, totalttc,totalht,totalqte,document,"");
    return Collections.singletonList(result);
        
       // return map.values().stream()
           //     .map(summary -> new CAglobalDTO(dateDebut, dateFin, total))
            //    .collect(Collectors.toList());
    }
 
 
 
 
  
 
 }
