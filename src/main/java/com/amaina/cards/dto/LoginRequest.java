package com.amaina.cards.dto;

import lombok.Builder;

@Builder
public record LoginRequest (String email, String password ){}
