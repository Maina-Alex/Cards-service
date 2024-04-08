package com.amaina.cards.unitTest;

import com.amaina.cards.constant.CardSort;
import com.amaina.cards.constant.CardStatus;
import com.amaina.cards.constant.Role;
import com.amaina.cards.dto.CardFilterDto;
import com.amaina.cards.dto.CardListResponse;
import com.amaina.cards.dto.CreateCardDto;
import com.amaina.cards.dto.UpdateCardDto;
import com.amaina.cards.exception.InvalidRequestException;
import com.amaina.cards.exception.ResourceNotFoundException;
import com.amaina.cards.exception.UnAuthorizedOperationException;
import com.amaina.cards.model.AppUser;
import com.amaina.cards.model.Card;
import com.amaina.cards.repository.CardRepository;
import com.amaina.cards.service.CardService;
import com.amaina.cards.service.CardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private Card mockCard;
    @Mock
    private AppUser appUser;
    private CardService cardService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        cardService= new CardServiceImpl(cardRepository);
        when(appUser.getId()).thenReturn(1L);
    }

    @Test
    void createCard(){
        CreateCardDto createCardDto = CreateCardDto.builder()
                .name("Test Card")
                .description("This is a testing card")
                .color("#FFFFFF")
                .build();
        cardService.createCard(createCardDto,appUser);
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void createCardWithInvalidColorThrowsException(){
        CreateCardDto createCardDto = CreateCardDto.builder()
                .name("Test Card")
                .description("This is a testing card")
                .color("FFFFF")
                .build();
        Assertions.assertThrows(InvalidRequestException.class, ()-> cardService.createCard(createCardDto,appUser));
        verify(cardRepository, times(0)).save(any(Card.class));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void searchTasks(Role role){
        when(appUser.getRole()).thenReturn(role);
        CardFilterDto cardFilterDto= CardFilterDto.builder()
                .createdDate(new Date())
                .cardSort(CardSort.STATUS)
                .name("Test Card")
                .pageable(PageRequest.of(0,10))
                .description("Testing card")
                .build();
        Page<Card> pageWithMockCard = new PageImpl<>(Collections.singletonList(mockCard));
        when(cardRepository.findAll(any(Specification.class),any(Pageable.class)))
                .thenReturn(pageWithMockCard);
        CardListResponse cardListResponse = cardService.filterCards(cardFilterDto,appUser);
        assertThat(cardListResponse.pages()).isEqualTo(1);
        assertThat(cardListResponse.totalRecords()).isEqualTo(1);
        assertThat(cardListResponse.cards().size()).isEqualTo(1);
    }

    @Test
    void updateCardSuccess(){
        UpdateCardDto updateCardDto= UpdateCardDto.builder()
                .cardId(1L)
                .description("Testing card")
                .color("#FFF000")
                .cardStatus(CardStatus.IN_PROGRESS)
                .build();
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(mockCard));
        when(mockCard.getAppUser()).thenReturn(appUser);
        cardService.updateCard(updateCardDto,appUser);
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(mockCard));
        verify(mockCard).setColor(anyString());
        verify(mockCard).setStatus(CardStatus.IN_PROGRESS);
        verify(mockCard).setDescription(anyString());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void UpdateCardWithInvalidIdThrowsResourceNotFoundException(){
        UpdateCardDto updateCardDto= UpdateCardDto.builder()
                .description("Testing card")
                .color("#FFF000")
                .cardStatus(CardStatus.IN_PROGRESS)
                .build();
        when(cardRepository.findById(anyLong())).thenReturn(null);
        assertThatThrownBy(()-> cardService.updateCard(updateCardDto,appUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void UpdateCardOtherMembersCardWithoutAdminPermissionException(){
        UpdateCardDto updateCardDto= UpdateCardDto.builder()
                .description("Testing card")
                .color("#FFF000")
                .cardStatus(CardStatus.IN_PROGRESS)
                .build();
        AppUser appUser1 = mock(AppUser.class);
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(mockCard));
        when(mockCard.getAppUser()).thenReturn(appUser1);
        when(appUser.getRole()).thenReturn(Role.MEMBER);
        assertThatThrownBy(()-> cardService.updateCard(updateCardDto,appUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCardSuccess(){
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(mockCard));
        when(mockCard.getAppUser()).thenReturn(appUser);
        cardService.deleteCard(1L,appUser);
        verify(cardRepository).delete(any(Card.class));
    }


    @Test
    void deleteCardWithInvalidIdThrowsException(){
        when(cardRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()-> cardService.deleteCard(1L, appUser)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCardOtherMembersWithoutAdminPermissionThrowsException(){
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(mockCard));
        when(appUser.getRole()).thenReturn(Role.MEMBER);
        AppUser appUser1 = mock(AppUser.class);
        when(mockCard.getAppUser()).thenReturn(appUser1);
        assertThatThrownBy(()-> cardService.deleteCard(1L,appUser)).isInstanceOf(UnAuthorizedOperationException.class);
    }


}
