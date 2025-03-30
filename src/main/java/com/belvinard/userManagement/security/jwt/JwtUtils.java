package com.belvinard.userManagement.security.jwt;

import com.belvinard.userManagement.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;


    //private final int jwtExpirationMs = 86400000; // 1 jour

//    private SecretKey getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret); // Convertit en bytes
//        return Keys.hmacShaKeyFor(keyBytes); // Génère une clé compatible HS512
//    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        if (keyBytes.length < 64) {
            throw new WeakKeyException("La clé secrète doit avoir au moins 512 bits pour HS512.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS512) // Correction ici !
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // Correction ici aussi !
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // Correction ici aussi !
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
