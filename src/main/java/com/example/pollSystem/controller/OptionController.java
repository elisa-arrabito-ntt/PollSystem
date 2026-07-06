package com.example.pollSystem.controller;

import com.example.pollSystem.dto.request.CreateOptionRequestDto;
import com.example.pollSystem.dto.response.OptionResponseDto;
import com.example.pollSystem.service.OptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/v0")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @PostMapping("/polls/{id}/options")
    public ResponseEntity<OptionResponseDto> addOption(
            @PathVariable("id") Long pollId,
            @Valid @RequestBody CreateOptionRequestDto request
    ) {
        OptionResponseDto response = optionService.addOptionToPoll(pollId, request);
        return ResponseEntity.status(201).body(response);
    }
}