package com.koerber.order_service.Config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class AppConfigTest {
    @Test
    void restTemplateFactoryMethod() {
        AppConfig spyConfig = Mockito.spy(new AppConfig());
        RestTemplate rt = spyConfig.restTemplate();

        assertNotNull(rt, "restTemplate() should return a RestTemplate instance");
        verify(spyConfig, times(1)).restTemplate();
    }
}
