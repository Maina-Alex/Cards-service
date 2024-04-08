package com.amaina.cards.service.util;

import com.amaina.cards.dto.CardFilterDto;
import com.amaina.cards.exception.InvalidRequestException;
import com.amaina.cards.model.Card;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CardUtil {
    public static void validateCardColor(String color){
        if(color!=null){
            String colorRegex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
            if(!color.matches(colorRegex)){
                log.error("Invalid color passed");
                throw new InvalidRequestException("Invalid color, Color needs to be alphanumeric 6 String prefixed by #");
            }
        }
    }

    /**
     *  Creates a query specification to filter cards
     * @param cardFilterDto CardFilterDto
     * @param appUserId Creating user Id
     * @return Specification of  Card
     */
    public static Specification<Card> buildCardSpecification(CardFilterDto cardFilterDto, Optional<Long> appUserId){
        return (root, query, criteriaBuilder)-> {
            List<Predicate> predicates = new ArrayList<>();
            Optional.ofNullable(cardFilterDto.name())
                    .ifPresent(name-> predicates.add(criteriaBuilder.equal(root.get("name"),cardFilterDto.name())));
            Optional.of(cardFilterDto.cardStatus().name())
                    .ifPresent(status-> predicates.add(criteriaBuilder.equal(root.get("status"),status)));
            Optional.ofNullable(cardFilterDto.createdDate())
                    .ifPresent(createdDate-> {
                        Instant instant = createdDate.toInstant();
                        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                        LocalDate date = zonedDateTime.toLocalDate();
                        LocalDateTime startOfDay = date.atStartOfDay();
                        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
                        Instant endOfDayInstant = endOfDay.atZone(ZoneId.systemDefault()).toInstant();
                        Instant startDayInstant = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
                        Date endDate = Date.from(endOfDayInstant);
                        Date startDate = Date.from(startDayInstant);
                        predicates.add(criteriaBuilder.between(root.get("createdDate"),startDate,endDate));
                    });
            Optional.ofNullable(cardFilterDto.color())
                    .ifPresent(color-> predicates.add(criteriaBuilder.equal(root.get("color"),cardFilterDto.color())));
            appUserId.ifPresent(userId-> predicates.add(criteriaBuilder.equal(root.get("appUserId"), userId)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
