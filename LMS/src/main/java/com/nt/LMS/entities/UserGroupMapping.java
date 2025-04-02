package com.nt.LMS.entities;


import com.nt.LMS.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_group")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserGroupMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_group_id;

//    @ManyToOne
//    @JoinColumn(name = "group_id")
//    private Group group;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;



}
