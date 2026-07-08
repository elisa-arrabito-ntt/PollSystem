package com.example.pollSystem.service;

import com.example.pollSystem.dto.request.OptionRequestDto;
import com.example.pollSystem.dto.response.OptionResponseDto;
import com.example.pollSystem.entity.Option;
import com.example.pollSystem.entity.Poll;
import com.example.pollSystem.entity.PollStatus;
import com.example.pollSystem.exception.*;
import com.example.pollSystem.mapper.OptionMapper;
import com.example.pollSystem.repository.OptionRepository;
import com.example.pollSystem.repository.PollRepository;
import java.time.LocalDateTime;

import com.example.pollSystem.repository.VoteRepository;
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
    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    @Transactional
    public OptionResponseDto addOptionToPoll(Long pollId, OptionRequestDto request) {
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

    @Transactional
    public OptionResponseDto updateOption(Long pollId, Long optionId, OptionRequestDto request) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> {
                    log.warn("Poll with id {} not found when updating option", pollId);
                    return new PollNotFoundException("Poll not found");
                });

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.warn("Option with id {} not found when updating on poll {}", optionId, pollId);
                    return new OptionNotFoundException("Option not found");
                });

        if (!option.getPoll().getId().equals(poll.getId())) {
            log.warn("Option {} does not belong to poll {}: update rejected", optionId, pollId);
            throw new InvalidOptionOperationException("Option does not belong to the given poll");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        if (!poll.getOwner().equals(currentUser)) {
            log.warn("User {} attempted to update option in poll {} owned by {}",
                    currentUser, pollId, poll.getOwner());
            throw new PollOwnershipException("Only the owner can update options in this poll");
        }

        if (poll.getStatus() != PollStatus.ACTIVE) {
            log.warn("User {} attempted to update option in non-active poll {} (status={})",
                    currentUser, pollId, poll.getStatus());
            throw new PollNotModifiableException("Cannot update options in a non-active poll");
        }

        // Check che option non abbia voti
        long voteCount = voteRepository.countByOptionId(optionId);
        if (voteCount > 0) {
            log.warn("User {} attempted to update option {} with {} votes on poll {}",
                    currentUser, optionId, voteCount, pollId);
            throw new OptionNotModifiableException("Cannot update option that has votes");
        }

        option.setMessage(request.getMessage());

        Option updated = optionRepository.save(option);

        log.info("Option '{}' updated in poll {} by owner {}",
                updated.getMessage(), pollId, currentUser);

        return optionMapper.toResponseDto(updated);
    }


    @Transactional
    public void deleteOption(Long pollId, Long optionId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> {
                    log.warn("Poll with id {} not found when deleting option {}", pollId, optionId);
                    return new PollNotFoundException("Poll not found");
                });

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> {
                    log.warn("Option with id {} not found when deleting on poll {}", optionId, pollId);
                    return new OptionNotFoundException("Option not found");
                });

        if (!option.getPoll().getId().equals(poll.getId())) {
            log.warn("Option {} does not belong to poll {}: delete rejected", optionId, pollId);
            throw new InvalidOptionOperationException("Option does not belong to the given poll");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        if (!poll.getOwner().equals(currentUser)) {
            log.warn("User {} attempted to delete option {} on poll {} owned by {}",
                    currentUser, optionId, pollId, poll.getOwner());
            throw new PollOwnershipException("Only the owner can delete options of this poll");
        }

        if (poll.getStatus() != PollStatus.ACTIVE) {
            log.warn("User {} attempted to delete option {} on non-active poll {} (status={})",
                    currentUser, optionId, pollId, poll.getStatus());
            throw new PollNotModifiableException("Cannot delete options of a non-active poll");
        }

        // Check che option non abbia voti
        long voteCount = voteRepository.countByOptionId(optionId);
        if (voteCount > 0) {
            log.warn("User {} attempted to delete option {} with {} votes on poll {}",
                    currentUser, optionId, voteCount, pollId);
            throw new OptionNotModifiableException("Cannot delete option that has votes");
        }

        optionRepository.delete(option);

        log.info("Option {} deleted from poll {} by owner {}", optionId, pollId, currentUser);
    }

}