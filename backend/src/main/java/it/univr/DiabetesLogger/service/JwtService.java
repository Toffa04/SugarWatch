package it.univr.DiabetesLogger.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.univr.DiabetesLogger.model.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${jwt.secret}")

    private String key;

    public String generationToken(String email, Role role){
        String roleString = role != null ? role.toString() : "NONE";

        return Jwts.builder()
                .claims()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .add("role", roleString)
                .and()
                .signWith(getKey())
                .compact();
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token){
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class);
        return role != null ? role : "NONE";
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        final SimpleGrantedAuthority role =
                new SimpleGrantedAuthority(extractRole(token));

        return email.equals(userDetails.getUsername())
                && userDetails.getAuthorities().contains(role)
                && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        return claimResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private SecretKey getKey(){
        byte[] bytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(bytes);
    }
}
