package com.example.pollSystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "polls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    // username dell'owner (FK ???)
    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PollStatus status;
}