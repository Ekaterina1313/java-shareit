package ru.practicum.shareit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

    @Bean
    public RestTemplate restTemplate() {
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return new RestTemplate(factory);
    }
}