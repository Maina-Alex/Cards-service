package com.amaina.cards.model;

import com.amaina.cards.constant.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phone;
    private String address;
    private boolean accountActive;
    @Enumerated(EnumType.STRING)
    private Role role;
}
