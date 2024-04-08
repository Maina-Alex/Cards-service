package com.amaina.cards.dto;

import com.amaina.cards.constant.CardSort;
import com.amaina.cards.constant.CardStatus;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * DTO for {@link com.amaina.cards.model.Card}
 */
@Builder
public record CardFilterDto(
    String name,
    String description,
    String color,
    CardStatus cardStatus,
    Date createdDate,
    CardSort cardSort,
    Pageable pageable
){}