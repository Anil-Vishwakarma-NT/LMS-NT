package com.nt.LMS.entities;


import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private long creatorId;

    public Group(String name  , long creatorId){
        this.groupName = name;
        this.creatorId = creatorId;
    }



}
