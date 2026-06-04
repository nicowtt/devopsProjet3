package com.openclassrooms.dataShare.mapper;

import com.openclassrooms.dataShare.dto.FileRequestDTO;
import com.openclassrooms.dataShare.dto.FileResponseDTO;
import com.openclassrooms.dataShare.entities.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FileDTOMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    File toEntity(FileRequestDTO fileRequestDTO);

    @Mapping(target = "hasPassword", expression = "java(file.getPassword() != null && !file.getPassword().isBlank())")
    FileResponseDTO toFileResponseDTO(File file);
}
