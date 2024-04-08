package com.amaina.cards.dto;

import com.amaina.cards.model.Card;
import lombok.Builder;

import java.util.List;

@Builder
public record CardListResponse (List<Card> cards, int pages, long totalRecords){}
