package com.openclassrooms.dataShare.mapper;

import com.openclassrooms.dataShare.dto.FileDTO;
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
    File toEntity(FileDTO fileDTO);

    @Mapping(target = "dayBeforeExpiration", ignore = true)
    @Mapping(target = "password", ignore = true)
    FileDTO toDTO(File file);
}
