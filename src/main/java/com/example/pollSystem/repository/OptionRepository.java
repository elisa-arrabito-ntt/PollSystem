package com.example.pollSystem.repository;

import com.example.pollSystem.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}