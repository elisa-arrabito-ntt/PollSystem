package com.example.pollSystem.controller;

import com.example.pollSystem.dto.request.CreatePollRequestDto;
import com.example.pollSystem.dto.response.PollListPageResponseDto;
import com.example.pollSystem.dto.response.PollResponseDto;
import com.example.pollSystem.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/v0")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @PostMapping("/polls")
    public ResponseEntity<PollResponseDto> createPoll(@Valid @RequestBody CreatePollRequestDto request) {
        PollResponseDto response = pollService.createPoll(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/polls")
    public ResponseEntity<PollListPageResponseDto> getPolls(
            @RequestParam(required = false) String search,
            @RequestParam int page,
            @RequestParam int size
    ) {
        PollListPageResponseDto response = pollService.getPolls(search, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/polls/{id}")
    public ResponseEntity<PollResponseDto> getPollById(@PathVariable Long id) {
        PollResponseDto response = pollService.getPollById(id);
        return ResponseEntity.ok(response);
    }
}