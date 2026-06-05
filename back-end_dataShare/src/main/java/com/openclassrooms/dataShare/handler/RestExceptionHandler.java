package com.openclassrooms.dataShare.handler;

import com.openclassrooms.dataShare.exception.FileSizeExceededException;
import com.openclassrooms.dataShare.exception.FileStorageException;
import com.openclassrooms.dataShare.exception.InvalidFileTypeException;
import com.openclassrooms.dataShare.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException runtimeException, WebRequest request) {
        logError(runtimeException);
        return handleExceptionInternal(runtimeException, getErrorDetails(runtimeException, request), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {BadCredentialsException.class})
    protected ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException badCredentialsException,
                                                                   WebRequest request) {
        logError(badCredentialsException);
        return handleExceptionInternal(badCredentialsException, getErrorDetails(badCredentialsException, request),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<Object> handleForbiddenException(AccessDeniedException accessDeniedException,
                                                              WebRequest request) {
        logError(accessDeniedException);
        return handleExceptionInternal(accessDeniedException, getErrorDetails(accessDeniedException, request),
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
    @ExceptionHandler(value = {FileSizeExceededException.class})
    protected ResponseEntity<Object> handleFileSizeExceededException(FileSizeExceededException ex, WebRequest request) {
        logError(ex);
        return handleExceptionInternal(ex, getErrorDetails(ex, request),
                new HttpHeaders(), HttpStatus.CONTENT_TOO_LARGE, request);
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(value = {InvalidFileTypeException.class})
    protected ResponseEntity<Object> handleInvalidFileTypeException(InvalidFileTypeException invalidFileTypeException, WebRequest request) {
        logError(invalidFileTypeException);
        return handleExceptionInternal(invalidFileTypeException, getErrorDetails(invalidFileTypeException, request),
                new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(value = {FileStorageException.class})
    protected ResponseEntity<Object> handleFileStorageException(FileStorageException fileStorageException, WebRequest request) {
        logError(fileStorageException);
        return handleExceptionInternal(fileStorageException, getErrorDetails(fileStorageException, request),
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(RuntimeException runtimeException, WebRequest request) {
        logError(runtimeException);
        return handleExceptionInternal(runtimeException, "Internal Server error", new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException,
        WebRequest request) {
        logError(resourceNotFoundException);
        return handleExceptionInternal(resourceNotFoundException, getErrorDetails(resourceNotFoundException, request),
            new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    private void logError(Exception exception) {
        logger.error(exception.getMessage(), exception);
    }

    private ErrorDetails getErrorDetails(Exception exception, WebRequest request) {
        return new ErrorDetails(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));
    }
}
