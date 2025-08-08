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
    String[] labels;
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

 public List<CAglobalDTO> getChiffreAffairePeriode(LocalDate dateDebut, LocalDate dateFin, String modeDate, Long id, String inclureBLs, String groupBy) {
    List<DataApiEntity> lignes = dataApiClient.fetchData(dateDebut, dateFin, inclureBLs, modeDate, id);
    System.out.println(lignes.size() + " lignes récupérées entre " + dateDebut + " et " + dateFin);

    Map<String, MonthSummary> map = new TreeMap<>();
    Integer document = 0;
    WeekFields weekFields = WeekFields.ISO;
    
        
    for (DataApiEntity ligne : lignes) {
        LocalDate date = "dateBL".equalsIgnoreCase(modeDate) ? ligne.getDateBL() : ligne.getDate();

        if (date == null || date.isBefore(dateDebut) || date.isAfter(dateFin)) continue;
        if (ligne.getTtc() == null || ligne.getTtc() == 0.0) continue;
        if (ligne.getHt() == null || ligne.getHt() == 0.0) continue;
        if (ligne.getQte() == null || ligne.getQte() == 0.0) continue;

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
                    key = ligne.getCoNom();
                    break;
                } else continue;
            case "article":
                if (ligne.getDesignation() != null) {
                    key = ligne.getDesignation();
                    break;
                } else continue;
            case "client":
                if (ligne.getCtIntitule() != null) {
                    key = ligne.getCtIntitule();
                    break;
                } else continue;
            case "depot":
                if (ligne.getDepot() != null) {
                    key = ligne.getDepot();
                    break;
                } else continue;
            case "mois":
            default:
                key = date.getYear() + "-" + String.format("%02d", date.getMonthValue()); 
                break;
        }

        map.putIfAbsent(key, new MonthSummary());
        map.get(key).update(date, ligne.getTtc(), ligne.getHt(), ligne.getQte());
    }

   
    List<CAglobalDTO> dtoList = map.entrySet().stream()
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

    if ("client".equalsIgnoreCase(groupBy)) {
           List<String> labels = new ArrayList<>();
    for (CAglobalDTO dto : dtoList) {
        labels.add(dto.getLabel());
    }
       

      
        
    }
    
    
    return dtoList;
}
public String[] getClientList(LocalDate dateDebut, LocalDate dateFin, String modeDate, String inclureBLs, String groupBy, Long id){
      List<CAglobalDTO> dtoList = getChiffreAffairePeriode(dateDebut, dateFin, modeDate, id, inclureBLs, "client");

    
    List<String> labels = dtoList.stream()
                                 .map(CAglobalDTO::getLabel)
                                 .collect(Collectors.toList());

    System.out.println( labels);

    return labels.toArray(new String[0]);
}
public List<CAglobalDTO> getEvolutionClientParMois(LocalDate dateDebut, LocalDate dateFin, String client, String modeDate, Long id, String inclureBLs) {
       List<DataApiEntity> lignes = dataApiClient.fetchData(dateDebut, dateFin,"mois", inclureBLs, id);
    System.out.println(dateDebut);
    System.out.println(dateFin);

    Map<String, MonthSummary> map = new TreeMap<>(); 

    for (DataApiEntity ligne : lignes) {
          LocalDate date = "dateBL".equalsIgnoreCase(modeDate) ? ligne.getDateBL() : ligne.getDate();
         
        if (date == null || date.isBefore(dateDebut) || date.isAfter(dateFin)) continue;
        if (ligne.getCtIntitule() == null || !ligne.getCtIntitule().equals(client)) continue; 
        if (ligne.getTtc() == null || ligne.getTtc() == 0.0) continue;
        if (ligne.getHt() == null || ligne.getHt() == 0.0) continue;
        if (ligne.getQte() == null || ligne.getQte() == 0.0) continue;

       
        String key = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        System.out.println(key);
        map.putIfAbsent(key, new MonthSummary());
        map.get(key).update(date, ligne.getTtc(), ligne.getHt(), ligne.getQte());
    }

    List<CAglobalDTO> evolution = map.entrySet().stream()
        .map(entry -> {
            String key = entry.getKey();
            MonthSummary summary = entry.getValue();
            String label = generateLabel(key, "mois", summary);
            System.out.println(label);
            return new CAglobalDTO(
                summary.minDate,
                summary.maxDate,
                summary.total,
                summary.totalht,
                summary.totalqte,
                0, 
                label 
            );
        })
        .collect(Collectors.toList());
        System.out.println(evolution);

    return evolution;
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
           
        
            
        }
     
 CAglobalDTO result = new CAglobalDTO(dateDebut, dateFin, totalttc,totalht,totalqte,document,"");
    return Collections.singletonList(result);
      
    }
 
 
 
 
  
 
 }
