package com.skodin.services;

import com.skodin.models.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
//@PropertySource("file:/my-task-tracker/.env")
public class JwtService {

    // TODO: 001 вынести
//    @Value("${SECRET}")
    private static final String SECRET_KEY = "gSlrTZoP7iFZP6c1m9f6h1JwNP2X89+y0JHQ3EXo/hklG2euXqSMEbfpMqY0KaqH";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Long extractId(String token) {

        if (token.startsWith("Bearer ")){
            token = token.substring(7);
        }

        return Long.parseLong(Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        UserEntity entity = (UserEntity) userDetails;
        Map<String, String> extractClaims = Collections.singletonMap("id", entity.getId().toString());
        return generateToken(extractClaims, userDetails);
    }

    public String generateToken(Map<String, String> extractClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetailsService userDetailsService){
        try {
            String username = extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final String username1 = extractUsername(token);
            return (username1.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
