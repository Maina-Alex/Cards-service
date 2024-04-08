package com.amaina.cards.dto;

import com.amaina.cards.constant.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCardDto(@NotNull(message = "Card id required") Long cardId,
                            String color,
                            String description,
                            CardStatus cardStatus) {
}
