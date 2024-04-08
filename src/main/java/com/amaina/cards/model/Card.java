package com.amaina.cards.model;

import com.amaina.cards.constant.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 *  Model to create task
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private String color;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    @Column(name = "created_date")
    private Date createdDate;
    @ManyToOne
    private AppUser appUser;
}
