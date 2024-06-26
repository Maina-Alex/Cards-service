package com.amaina.cards.repository;

import com.amaina.cards.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardRepository extends JpaRepository<Card,Long>, JpaSpecificationExecutor<Card> {
}
