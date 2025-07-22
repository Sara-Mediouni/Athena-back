package com.example.demo.API.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Configuration
public class ConfigRest {







    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Parcours des convertisseurs pour configurer Jackson
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                // Enregistre JavaTimeModule pour gérer LocalDateTime
                mapper.registerModule(new JavaTimeModule());
                // Désactive les timestamps en format numérique
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        }
        return restTemplate;
    }
 
}
