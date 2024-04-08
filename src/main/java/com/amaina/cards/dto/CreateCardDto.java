package com.amaina.cards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateCardDto (
        @NotNull(message = "Card name is required")
        String name,
        String description,
        String color
){}
