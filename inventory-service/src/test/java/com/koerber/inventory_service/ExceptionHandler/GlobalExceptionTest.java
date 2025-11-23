package com.koerber.inventory_service.ExceptionHandler;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GlobalExceptionTest {
    @Autowired
    GlobalException globalException;

    @Test
    void handleNoResourceFoundException() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getMessage()).thenReturn("No static resource h2-console for request '/h2-console'.");

        ResponseEntity<Map<String, Object>> response =globalException.handleNoResourceFoundException(ex);

        Map<String, Object> body = response.getBody();
        assertEquals(404, body.get("status"));
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = mock(IllegalArgumentException.class);
        when(ex.getMessage()).thenReturn("No static resource h2-console for request '/h2-console'.");

        ResponseEntity<Map<String, Object>> response =globalException.handleIllegalArgumentException(ex);

        Map<String, Object> body = response.getBody();
        assertEquals(400, body.get("status"));
    }

    @Test
    void handleException() {
        Exception ex = mock(Exception.class);
        when(ex.getMessage()).thenReturn("No static resource h2-console for request '/h2-console'.");

        ResponseEntity<Map<String, Object>> response =globalException.handleAllExceptions(ex);

        Map<String, Object> body = response.getBody();
        assertEquals(500, body.get("status"));
    }

    @Test
    void handleProductNotExistException() {
        ProductNotExistException ex = mock(ProductNotExistException.class);
        when(ex.getMessage()).thenReturn("No static resource h2-console for request '/h2-console'.");

        ResponseEntity<Map<String, Object>> response =globalException.handleProductNotExistException(ex);

        Map<String, Object> body = response.getBody();
        assertEquals(404, body.get("status"));
    }
}
