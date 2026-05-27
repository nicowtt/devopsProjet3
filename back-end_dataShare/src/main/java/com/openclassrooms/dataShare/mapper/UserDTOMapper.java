package com.openclassrooms.dataShare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserDTOMapper {
}
