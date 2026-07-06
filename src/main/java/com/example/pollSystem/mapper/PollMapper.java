package com.example.pollSystem.mapper;

import com.example.pollSystem.dto.request.CreatePollRequestDto;
import com.example.pollSystem.dto.response.PollResponseDto;
import com.example.pollSystem.entity.Poll;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PollMapper {

    // owner e status gestiti nel service, id dal db
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "status", ignore = true)
    Poll toEntity(CreatePollRequestDto dto);

    PollResponseDto toResponseDto(Poll poll);
}