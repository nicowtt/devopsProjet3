package com.openclassrooms.dataShare.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileTest {

    @Test
    void test_file_getters_setters() {
        // GIVEN
        File file = new File();
        LocalDateTime now = LocalDateTime.now();

        // WHEN
        file.setName("document.pdf");
        file.setSize(2048L);
        file.setMimeType("application/pdf");
        file.setExpiredAt(now.plusDays(7));
        file.setPassword("secret");

        // THEN
        assertThat(file.getName()).isEqualTo("document.pdf");
        assertThat(file.getSize()).isEqualTo(2048L);
        assertThat(file.getMimeType()).isEqualTo("application/pdf");
        assertThat(file.getExpiredAt()).isEqualTo(now.plusDays(7));
        assertThat(file.getPassword()).isEqualTo("secret");
    }

    @Test
    void test_new_file_has_null_owner() {
        assertThat(new File().getOwner()).isNull();
    }

    @Test
    void test_new_file_has_null_password() {
        assertThat(new File().getPassword()).isNull();
    }

    @Test
    void test_file_owner_is_set() {
        // GIVEN
        File file = new File();
        User user = new User();
        user.setEmail("owner@gmail.com");

        // WHEN
        file.setOwner(user);

        // THEN
        assertThat(file.getOwner()).isEqualTo(user);
        assertThat(file.getOwner().getEmail()).isEqualTo("owner@gmail.com");
    }

    @Test
    void test_expired_at_is_after_created_at() {
        // GIVEN
        LocalDateTime createdAt = LocalDateTime.now();
        File file = new File();

        // WHEN
        file.setExpiredAt(createdAt.plusDays(3));

        // THEN
        assertThat(file.getExpiredAt()).isAfter(createdAt);
    }
}