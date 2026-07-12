package com.example.pollSystem.repository;

import com.example.pollSystem.entity.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {

    // genera una query tipo WHERE lower(question) LIKE lower('%search%')
    // Pageable consente di specificare page e size, tradotto in SQL con LIMIT e OFFSET
    Page<Poll> findByQuestionContainingIgnoreCase(String search, Pageable pageable);

    List<Poll> findByExpiresAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}