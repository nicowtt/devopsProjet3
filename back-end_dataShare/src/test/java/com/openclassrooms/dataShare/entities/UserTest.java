package com.openclassrooms.dataShare.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void test_valid_user_no_violations() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // THEN
        assertThat(violations).isEmpty();
    }

    @Test
    void test_null_email_has_violation() {
        // GIVEN
        User user = new User();
        user.setEmail(null);
        user.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void test_blank_email_has_violation() {
        // GIVEN
        User user = new User();
        user.setEmail("   ");
        user.setPassword(PASSWORD);

        // WHEN
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void test_short_password_has_violation() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword("short");

        // WHEN
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void test_getUsername_returns_email() {
        // GIVEN
        User user = new User();
        user.setEmail(EMAIL);

        // THEN
        assertThat(user.getUsername()).isEqualTo(EMAIL);
    }

    @Test
    void test_getPassword_returns_password() {
        // GIVEN
        User user = new User();
        user.setPassword(PASSWORD);

        // THEN
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void test_getAuthorities_returns_empty_list() {
        // GIVEN
        User user = new User();

        // THEN
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void test_account_is_non_expired() {
        // GIVEN
        User user = new User();

        // THEN
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    void test_account_is_non_locked() {
        // GIVEN
        User user = new User();

        // THEN
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    void test_credentials_are_non_expired() {
        // GIVEN
        User user = new User();

        // THEN
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void test_account_is_enabled() {
        // GIVEN
        User user = new User();

        // THEN
        assertThat(user.isEnabled()).isTrue();
    }
}