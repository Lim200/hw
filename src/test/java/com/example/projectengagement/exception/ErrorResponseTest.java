package com.example.projectengagement.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void createErrorResponse_recordWorksCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse(now, 400, "Bad request");

        assertThat(error.timestamp()).isEqualTo(now);
        assertThat(error.status()).isEqualTo(400);
        assertThat(error.message()).isEqualTo("Bad request");
    }
}

