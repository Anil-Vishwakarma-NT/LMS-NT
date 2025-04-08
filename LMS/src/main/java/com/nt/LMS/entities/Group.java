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
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_id;

    private String group_name;

    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    private long creator_id;


    public Group(){}
    public Group(String name  , long creator_id){
        this.group_name = name;
        this.creator_id = creator_id;
    }



}
