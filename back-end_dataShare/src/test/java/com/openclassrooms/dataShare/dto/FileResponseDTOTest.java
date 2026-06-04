package com.openclassrooms.dataShare.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileResponseDTOTest {

    @Test
    void test_new_dto_is_not_protected_by_default() {
        assertThat(new FileResponseDTO().isHasPassword()).isFalse();
    }

    @Test
    void test_getters_setters() {
        // GIVEN
        FileResponseDTO dto = new FileResponseDTO();
        UUID uuid = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

        // WHEN
        dto.setUuid(uuid);
        dto.setName("document.pdf");
        dto.setSize(2048L);
        dto.setCreatedAt(createdAt);
        dto.setExpiredAt(expiredAt);
        dto.setHasPassword(true);

        // THEN
        assertThat(dto.getUuid()).isEqualTo(uuid);
        assertThat(dto.getName()).isEqualTo("document.pdf");
        assertThat(dto.getSize()).isEqualTo(2048L);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(dto.isHasPassword()).isTrue();
    }
}
