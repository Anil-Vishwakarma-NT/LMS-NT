package com.nt.LMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long groupId;

    public UserGroup(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;

    }
}
