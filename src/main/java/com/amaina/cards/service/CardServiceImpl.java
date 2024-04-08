package com.amaina.cards.service;

import com.amaina.cards.constant.CardStatus;
import com.amaina.cards.constant.Role;
import com.amaina.cards.dto.*;
import com.amaina.cards.exception.ResourceNotFoundException;
import com.amaina.cards.exception.UnAuthorizedOperationException;
import com.amaina.cards.model.AppUser;
import com.amaina.cards.model.Card;
import com.amaina.cards.repository.CardRepository;
import com.amaina.cards.service.util.CardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.amaina.cards.service.util.CardUtil.validateCardColor;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    /**
     * Creates card and Marks it as a TO DO
     *
     * @param cardRequest CardRequest
     * @param appUser     User creating card
     * @return new Created Card
     */
    @Override
    public Card createCard(CreateCardDto cardRequest, AppUser appUser) {
        log.info("Creating card {} by user {}", cardRequest, appUser);
        if (cardRequest.color() != null) {
            validateCardColor(cardRequest.color());
        }
        Card newCard = Card.builder()
                .status(CardStatus.TO_DO)
                .createdDate(new Date())
                .name(cardRequest.name())
                .description(cardRequest.description())
                .color(cardRequest.color())
                .appUser(appUser)
                .build();
        return cardRepository.save(newCard);
    }

    /**
     *  Filters/ Searches Cards
     *  A Member User can only view their cards while Admin can view all cards
     * @param filter Card Filter
     * @param appUser AppUser
     * @return CardListResponse
     */
    @Override
    public CardListResponse filterCards(CardFilterDto filter, AppUser appUser) {
        Specification<Card> cardSpecification;
        if (appUser.getRole().equals(Role.MEMBER)) {
            cardSpecification = CardUtil.buildCardSpecification(filter, Optional.of(appUser.getId()));
        } else {
            //User is an admin
            cardSpecification = CardUtil.buildCardSpecification(filter, Optional.empty());
        }
        Page<Card> cardPage= cardRepository.findAll(cardSpecification, filter.pageable());
        return CardListResponse.builder()
                .totalRecords(cardPage.getTotalElements())
                .pages(cardPage.getTotalPages())
                .cards(cardPage.toList())
                .build();
    }

    @Override
    public Card updateCard(UpdateCardDto updateCardDto, AppUser appUser) {
        Card card = cardRepository.findById(updateCardDto.cardId())
                .orElseThrow(()-> new ResourceNotFoundException("Card not found by id"));
        if(updateCardDto.color()!=null){
            validateCardColor(updateCardDto.color());
        }
        if(card.getAppUser()!=appUser && appUser.getRole()!=Role.ADMIN) {
            throw new UnAuthorizedOperationException("Unauthorized operation");
        }
        card.setStatus(updateCardDto.cardStatus());
        card.setColor(updateCardDto.color());
        card.setDescription(updateCardDto.description());
        return cardRepository.save(card);
    }

    @Override
    public void deleteCard(long cardId, AppUser appUser) {
        Card card = cardRepository.findById(cardId).orElseThrow(()->
                new ResourceNotFoundException("Card not found by id"));
        if(card.getAppUser()!=appUser && appUser.getRole()!=Role.ADMIN) {
            throw new UnAuthorizedOperationException("Unauthorized operation");
        }
        cardRepository.delete(card);
    }
}
