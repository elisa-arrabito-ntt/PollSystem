package com.example.pollSystem.service;

import com.example.pollSystem.dto.request.CreatePollRequestDto;
import com.example.pollSystem.dto.response.PollListPageResponseDto;
import com.example.pollSystem.dto.response.PollResponseDto;
import com.example.pollSystem.entity.Poll;
import com.example.pollSystem.entity.PollStatus;
import com.example.pollSystem.mapper.PollMapper;
import com.example.pollSystem.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollMapper pollMapper;

    @Transactional // atomicità
    public PollResponseDto createPoll(CreatePollRequestDto request) {
        // recupero l'utente autenticato
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String ownerUsername = auth.getName(); // recupero lo username (subject del token)

        Poll poll = pollMapper.toEntity(request);

        poll.setOwner(ownerUsername);
        poll.setStatus(PollStatus.ACTIVE);

        Poll saved = pollRepository.save(poll);

        log.info("Poll '{}' created by user {}", saved.getQuestion(), ownerUsername);

        return pollMapper.toResponseDto(saved);
    }


    public PollListPageResponseDto getPolls(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // tradotto poi in query con LIMIT e OFFSET

        Page<Poll> pollPage;
        if (search == null || search.isBlank()) {
            pollPage = pollRepository.findAll(pageable);
        } else {
            pollPage = pollRepository.findByQuestionContainingIgnoreCase(search, pageable);
        }

        // mapping di Page<Poll> -> PollListPageResponseDto
        PollListPageResponseDto response = new PollListPageResponseDto();
        response.setFirst(pollPage.isFirst());
        response.setLast(pollPage.isLast());
        response.setSize(pollPage.getSize());
        response.setTotalElements(pollPage.getTotalElements());
        response.setTotalPages(pollPage.getTotalPages());
        response.setNumber(pollPage.getNumber());

        // mapping dei singoli Poll -> PollResponseDto
        List<PollResponseDto> contents = pollPage.getContent().stream()
                .map(pollMapper::toResponseDto)
                .toList();
        response.setContents(contents);

        return response;
    }
}