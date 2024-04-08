package com.amaina.cards.security;

import com.amaina.cards.dto.LoginResponse;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final String CARDS_ISSUER = "CardsApp";
    private final String CARDS_AUD= "CardsAudience";
    private final String  TOKEN_PREFIX = "Bearer ";

    @Value("${auth.token.signatureKey}")
    private  String signingKey;

    private  Key getSigningKey(){

        byte[] apiKeySecretBytes = Base64.getEncoder().encode(signingKey.getBytes());
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }
    public LoginResponse createToken(String email, String role) {
        Claims claims = Jwts.claims()
                .add("role",role)
                .build();
        Date tokenDate = new Date();
        long accessTokenValidity = 60 * 60 * 1000;
        Date tokenValidity = new Date(tokenDate.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        String token = Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuer(CARDS_ISSUER)
                .issuedAt(tokenDate)
                .audience().add(List.of(CARDS_AUD))
                .and()
                .expiration(tokenValidity)
                .signWith(getSigningKey() )
                .compact();
       return  LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600).build();
    }

    private Claims parseClaims(String token) {
        JwtParser parser= Jwts.parser().setSigningKey(getSigningKey())
                .requireIssuer(CARDS_ISSUER)
                .requireAudience(CARDS_AUD).build();
        return parser.parseSignedClaims(token).getPayload();
    }
    public  Claims decodeToken(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

}
