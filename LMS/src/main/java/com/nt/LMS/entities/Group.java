package com.nt.LMS.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "groups")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    private String groupName;

    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    private long creatorId;



    public Group(String name  , long creatorId){
        this.groupName = name;
        this.creatorId = creatorId;
    }



}
