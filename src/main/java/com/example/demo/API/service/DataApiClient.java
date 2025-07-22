package com.example.demo.API.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.API.Entity.DataApiEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class DataApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
  
    public List<DataApiEntity> fetchData(LocalDate debut, LocalDate fin, String InclureBLs, String modeDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String url="";
        if ((modeDate == "DateBL") && (InclureBLs == "true")){
            url="http://41.228.170.247:90/api/CABLDBL";
        }
        else if ((modeDate == "DateFacture") && (InclureBLs == "true")){
            url="http://41.228.170.247:90/api/CABLDF";
        }
        else if ((modeDate == "DateBL") && (InclureBLs == "false")){
            url="http://41.228.170.247:90/api/CANBLDBL";}
        else {
            url="http://41.228.170.247:90/api/CANBLDF";
        }
         
        String apiurl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("_debut", debut.format(formatter))
                .queryParam("_fin", fin.format(formatter))
                .toUriString();

        DataApiEntity[] result = restTemplate.getForObject(apiurl, DataApiEntity[].class);
        return Arrays.asList(result);
    }
}
