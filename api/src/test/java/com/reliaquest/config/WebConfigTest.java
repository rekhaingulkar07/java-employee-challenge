package com.reliaquest.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(classes = WebConfig.class)
class WebConfigTest {

    @Autowired
    private WebClient employeeWebClient;

    @Test
    void testEmployeeWebClientBeanExists() {
        assertNotNull(employeeWebClient, "WebClient bean should not be null");
        assertTrue(employeeWebClient instanceof WebClient, "Bean should be instance of WebClient");
    }
}
