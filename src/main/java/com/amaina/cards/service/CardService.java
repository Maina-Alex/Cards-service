package com.amaina.cards.service;

import com.amaina.cards.dto.*;
import com.amaina.cards.model.AppUser;
import com.amaina.cards.model.Card;

public interface CardService {
    Card createCard(CreateCardDto card, AppUser appUser);

    CardListResponse filterCards(CardFilterDto filter, AppUser appUser);

    Card updateCard(UpdateCardDto updateCardDto, AppUser appUser);

    void deleteCard(long card, AppUser appUser);
}
