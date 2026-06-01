package com.openclassrooms.dataShare.handler;

import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    private RestExceptionHandler restExceptionHandler;

    @BeforeEach
    void setUp() {
        restExceptionHandler = new RestExceptionHandler();
    }

    @Test
    void test_handleConflict_with_IllegalArgumentException_returns_400() {
        // GIVEN
        IllegalArgumentException exception = new IllegalArgumentException("invalid argument");
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleConflict(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void test_handleConflict_with_IllegalStateException_returns_400() {
        // GIVEN
        IllegalStateException exception = new IllegalStateException("invalid state");
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleConflict(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void test_handleBadCredentialsException_returns_401() {
        // GIVEN
        BadCredentialsException exception = new BadCredentialsException("bad credentials");
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleBadCredentialsException(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void test_handleForbiddenException_returns_403() {
        // GIVEN
        AccessDeniedException exception = new AccessDeniedException("access denied");
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleForbiddenException(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void test_handleFileStorageException_returns_503() {
        // GIVEN
        FileStorageException exception = new FileStorageException("disk error", new RuntimeException());
        when(webRequest.getDescription(false)).thenReturn("uri=/api/files");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleFileStorageException(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void test_handleException_returns_500() {
        // GIVEN
        RuntimeException exception = new RuntimeException("unexpected error");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleException(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void test_handleResourceNotFoundException_returns_404() {
        // GIVEN
        ResourceNotFoundException exception = new ResourceNotFoundException("resource not found");
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // WHEN
        ResponseEntity<Object> response = restExceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}