package com.example.pollSystem.mapper;

import com.example.pollSystem.dto.request.CreateOptionRequestDto;
import com.example.pollSystem.dto.response.OptionResponseDto;
import com.example.pollSystem.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "poll", ignore = true) // settato nel service leggendolo dal db con id del path
    Option toEntity(CreateOptionRequestDto dto);

    OptionResponseDto toResponseDto(Option option);
}