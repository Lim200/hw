package com.example.projectengagement.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ValidationExceptionHandlerTest {

    private ValidationExceptionHandler handler;

    @Mock
    private MethodArgumentNotValidException methodEx;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ValidationExceptionHandler();
    }

    @Test
    void handleValidationErrors_returnsMapOfFieldErrors() {
        FieldError error1 = new FieldError("object", "field1", "must not be blank");
        FieldError error2 = new FieldError("object", "field2", "must be a valid email");

        when(methodEx.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(methodEx);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        Map<String, String> errors = response.getBody();
        assertThat(errors).hasSize(2);
        assertThat(errors.get("field1")).isEqualTo("must not be blank");
        assertThat(errors.get("field2")).isEqualTo("must be a valid email");
    }
}

