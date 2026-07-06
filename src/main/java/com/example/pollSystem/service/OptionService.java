package com.example.pollSystem.service;

import com.example.pollSystem.dto.request.CreateOptionRequestDto;
import com.example.pollSystem.dto.response.OptionResponseDto;
import com.example.pollSystem.entity.Option;
import com.example.pollSystem.entity.Poll;
import com.example.pollSystem.entity.PollStatus;
import com.example.pollSystem.exception.PollNotFoundException;
import com.example.pollSystem.exception.PollNotModifiableException;
import com.example.pollSystem.exception.PollOwnershipException;
import com.example.pollSystem.mapper.OptionMapper;
import com.example.pollSystem.repository.OptionRepository;
import com.example.pollSystem.repository.PollRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionService {

    private final PollRepository pollRepository;
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    @Transactional
    public OptionResponseDto addOptionToPoll(Long pollId, CreateOptionRequestDto request) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> {
                    log.warn("Poll with id {} not found when adding option", pollId);
                    return new PollNotFoundException("Poll not found");
                });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        if (!poll.getOwner().equals(currentUser)) {
            log.warn("User {} attempted to add option to poll {} owned by {}",
                    currentUser, pollId, poll.getOwner());
            throw new PollOwnershipException("Only the owner can add options to this poll");
        }

        if (poll.getStatus() != PollStatus.ACTIVE) {
            log.warn("User {} attempted to add option to non-active poll {} (status={})",
                    currentUser, pollId, poll.getStatus());
            throw new PollNotModifiableException("Cannot add options to a non-active poll");
        }

        Option option = optionMapper.toEntity(request);

        option.setPoll(poll);
        option.setCreatedAt(LocalDateTime.now());

        Option saved = optionRepository.save(option);

        log.info("Option '{}' added to poll {} by owner {}",
                saved.getMessage(), pollId, currentUser);

        return optionMapper.toResponseDto(saved);
    }
}