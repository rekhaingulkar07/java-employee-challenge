package com.reliaquest.config;

import com.reliaquest.constants.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {
    @Bean(name = "employeeWebClient")
    WebClient webClient() {
        WebClient webClient = WebClient.builder().baseUrl(Constants.BASE_URL).build();
        return webClient;
    }
}
