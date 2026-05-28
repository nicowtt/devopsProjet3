package com.openclassrooms.dataShare.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterDTOTest {

    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "password";

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void test_valid_dto_no_violations() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isEmpty();
    }

    @Test
    void test_null_email_has_violation() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail(null);
        dto.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void test_blank_email_has_violation() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("   ");
        dto.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void test_invalid_email_format_has_violation() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("not-an-email");
        dto.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void test_null_password_has_violation() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail(EMAIL);
        dto.setPassword(null);

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void test_blank_password_has_violation() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail(EMAIL);
        dto.setPassword("   ");

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void test_short_password_has_violation() {
        // GIVEN
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail(EMAIL);
        dto.setPassword("short");

        // WHEN
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }
}