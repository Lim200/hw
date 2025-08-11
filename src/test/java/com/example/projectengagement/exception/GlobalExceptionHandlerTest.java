package com.example.projectengagement.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.*;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private MethodArgumentNotValidException methodEx;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolationException constraintEx;

    @Mock
    private ConstraintViolation<?> violation1;

    @Mock
    private ConstraintViolation<?> violation2;

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationError_returnsBadRequestWithMessages() {
        FieldError fieldError1 = new FieldError("object", "field1", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "field2", "size must be between 1 and 10");

        when(methodEx.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponse> response = handler.handleValidationError(methodEx);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
                .contains("field1: must not be blank")
                .contains("field2: size must be between 1 and 10");
    }

    @Test
    void handleConstraintViolation_returnsBadRequestWithMessages() {
        when(constraintEx.getConstraintViolations()).thenReturn(Set.of(violation1, violation2));

        when(violation1.getPropertyPath()).thenReturn(path1);
        when(path1.toString()).thenReturn("fieldA");
        when(violation1.getMessage()).thenReturn("must be positive");

        when(violation2.getPropertyPath()).thenReturn(path2);
        when(path2.toString()).thenReturn("fieldB");
        when(violation2.getMessage()).thenReturn("must not be null");

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(constraintEx);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        String msg = response.getBody().message();
        assertThat(msg).contains("fieldA: must be positive");
        assertThat(msg).contains("fieldB: must not be null");
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Внутренняя ошибка сервера: Something went wrong");
    }
}
