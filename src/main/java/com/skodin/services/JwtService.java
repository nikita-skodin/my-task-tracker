package com.skodin.services;

import com.skodin.exceptions.InvalidToken;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.auth.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JWTProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(Collections.emptyMap(), userDetails);
    }

    public String generateAccessToken(@NotNull Map<String, Object> extractClaims, UserDetails userDetails) {

        if (extractClaims == null) {
            extractClaims = Collections.emptyMap();
        }

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccess()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(Collections.emptyMap(), userDetails);
    }

    public String generateRefreshToken(@NotNull Map<String, Object> extractClaims, UserDetails userDetails) {

        if (extractClaims == null) {
            extractClaims = Collections.emptyMap();
        }

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefresh()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthenticationResponse refreshUserToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new InvalidToken("Token is invalid");
        }

        String username = extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new AuthenticationResponse(
                generateAccessToken(extractAllClaims(refreshToken), userDetails),
                generateRefreshToken(extractAllClaims(refreshToken), userDetails)
        );
    }

    public boolean isTokenValid(String token) {
        try {
            String username = extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String username1 = extractUsername(token);
            return (username1.equals(userDetails.getUsername())) && !isTokenExpired(token) && userDetails.isEnabled();
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

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
