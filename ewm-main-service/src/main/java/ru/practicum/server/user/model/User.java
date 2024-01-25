package ru.practicum.server.user.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ewm_user", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;

}
