package com.amaina.cards.controller;

import com.amaina.cards.constant.CardStatus;
import com.amaina.cards.dto.*;
import com.amaina.cards.constant.CardSort;
import com.amaina.cards.model.AppUser;
import com.amaina.cards.model.Card;
import com.amaina.cards.service.CardService;
import com.amaina.cards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/api/v1/cards")
@RestController
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final UserService userService;

    /**
     * Creates a Card
     * @param card Card
     * @param authentication Authentication
     * @return Created Card with Id
     */
    @PostMapping("/create")
    public ResponseEntity<Card> createCard(@RequestBody @Valid CreateCardDto card, Authentication authentication){
        AppUser appUser=userService.findUserByEmail(authentication.getName());
        return ResponseEntity.ok(cardService.createCard(card, appUser));
    }

    /**
     * Filters tasks
     * @param name Optional Card Name
     * @param color Optional Card color
     * @param status Optional Card Status, TODO,INPROGRESS or DONE
     * @param createdOn Optional CreatedOn
     * @param cardSort Optional CardSort [name, color,createdOn, status]
     * @param page Page with default 0
     * @param size Result limit Size with default 10
     * @param authentication Authentication User
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<CardListResponse> searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createdOn,
            @RequestParam(required = false) CardSort cardSort,
            @RequestParam(required = false, defaultValue = "0") int page ,
            @RequestParam(required = false, defaultValue = "10")int size,
            Authentication authentication){
        AppUser appUser=userService.findUserByEmail(authentication.getName());
        Pageable pageable;
        if(cardSort!=null){
            pageable =  PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,cardSort.name().toLowerCase()));
        }else {
            pageable = PageRequest.of(page, size);
        }
        CardFilterDto cardFilterDto = CardFilterDto.builder()
                .name(name)
                .color(color)
                .cardStatus(status)
                .createdDate(createdOn)
                .cardSort(cardSort)
                .pageable(pageable)
                .build();
        return ResponseEntity.ok(cardService.filterCards(cardFilterDto,appUser));
    }

    /**
     * Updates a card description, status and color
     * @param updateCardDto UpdateCardDto
     * @param authentication Authentication
     * @return updated Card
     */
    @PutMapping("/update")
    public ResponseEntity<Card> updateCard(@RequestBody @Valid UpdateCardDto updateCardDto,
                                           Authentication authentication){
        AppUser appUser=userService.findUserByEmail(authentication.getName());
        return ResponseEntity.ok(cardService.updateCard(updateCardDto,appUser ));
    }

    /**
     * Deletes Card by id
     * @param cardId Card Id
     * @param authentication Authentication
     */
    @DeleteMapping("/delete/{cardId}")
    public void deleteCard(@PathVariable long cardId,
                                                     Authentication authentication){
        AppUser appUser=userService.findUserByEmail(authentication.getName());
        cardService.deleteCard(cardId, appUser);
    }

}
