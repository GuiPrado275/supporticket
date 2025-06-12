package com.guilherme.supporticket.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Component
public class JWTUtil {

    @Value("${jwt.secret}") //do application properties
    private String secret;

    @Value("${jwt.expiration}") //do application properties
    private String expiration;

    public String generateToken(String email) { //Token que no futuro será usado para authenticar o user
        SecretKey key = getKeyBySecret();
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(key)
                .compact();
    }

    private SecretKey getKeyBySecret() { //encryption key
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return key;
    }

    public boolean isValidToken(String token) { //para verificar se o token é valido ou está expirado
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims)){
            String email = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if (Objects.nonNull(email) && Objects.nonNull(expirationDate) && now.after(expirationDate)){
                return true;
            }
        }
        return false;
    }

    public String getEmail(String token) { //para pegar o email pelo token
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims)){
            return claims.getSubject();
        }
        return null;
    }

    public Claims getClaims(String token) { //para gerar claims do token
        SecretKey key = getKeyBySecret();
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null; //se o objeto está nulo ou inválido, retorna isso
        }
    }

}
