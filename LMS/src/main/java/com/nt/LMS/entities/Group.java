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
@NoArgsConstructor
@ToString
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_id;

    private String group_name;

//    @ManyToMany(mappedBy = "groups")
//    private Set<User> users = new HashSet<>();



}
