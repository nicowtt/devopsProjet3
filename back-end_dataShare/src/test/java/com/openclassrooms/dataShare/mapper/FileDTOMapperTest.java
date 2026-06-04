package com.openclassrooms.dataShare.mapper;

import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {FileDTOMapperImpl.class})
class FileDTOMapperTest {

    @Autowired
    private FileDTOMapper mapper;

    @Test
    void test_hasPassword_is_true_when_password_set() {
        // GIVEN
        File file = new File();
        file.setPassword("secret");

        // WHEN
        FileResponseDTO dto = mapper.toFileResponseDTO(file);

        // THEN
        assertThat(dto.isHasPassword()).isTrue();
    }

    @Test
    void test_hasPassword_is_false_when_password_is_null() {
        // GIVEN
        File file = new File();

        // WHEN
        FileResponseDTO dto = mapper.toFileResponseDTO(file);

        // THEN
        assertThat(dto.isHasPassword()).isFalse();
    }

    @Test
    void test_hasPassword_is_false_when_password_is_blank() {
        // GIVEN
        File file = new File();
        file.setPassword("   ");

        // WHEN
        FileResponseDTO dto = mapper.toFileResponseDTO(file);

        // THEN
        assertThat(dto.isHasPassword()).isFalse();
    }
}