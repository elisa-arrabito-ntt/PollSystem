package com.example.pollSystem.repository;

import com.example.pollSystem.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByPollId(Long pollId);
}