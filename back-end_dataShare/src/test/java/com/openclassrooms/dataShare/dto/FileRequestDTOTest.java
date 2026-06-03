package com.openclassrooms.dataShare.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FileRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void test_valid_dto_no_violations() {
        // GIVEN
        FileRequestDTO dto = new FileRequestDTO();
        dto.setDayBeforeExpiration(7L);

        // WHEN
        Set<ConstraintViolation<FileRequestDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isEmpty();
    }

    @Test
    void test_null_dayBeforeExpiration_has_violation() {
        // GIVEN
        FileRequestDTO dto = new FileRequestDTO();
        dto.setDayBeforeExpiration(null);

        // WHEN
        Set<ConstraintViolation<FileRequestDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dayBeforeExpiration"));
    }

    @Test
    void test_zero_dayBeforeExpiration_has_violation() {
        // GIVEN
        FileRequestDTO dto = new FileRequestDTO();
        dto.setDayBeforeExpiration(0L);

        // WHEN
        Set<ConstraintViolation<FileRequestDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dayBeforeExpiration"));
    }

    @Test
    void test_dayBeforeExpiration_above_max_has_violation() {
        // GIVEN
        FileRequestDTO dto = new FileRequestDTO();
        dto.setDayBeforeExpiration(8L);

        // WHEN
        Set<ConstraintViolation<FileRequestDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dayBeforeExpiration"));
    }

    @Test
    void test_negative_dayBeforeExpiration_has_violation() {
        // GIVEN
        FileRequestDTO dto = new FileRequestDTO();
        dto.setDayBeforeExpiration(-1L);

        // WHEN
        Set<ConstraintViolation<FileRequestDTO>> violations = validator.validate(dto);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dayBeforeExpiration"));
    }
}