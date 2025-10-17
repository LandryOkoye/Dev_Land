package com.landryokoye.auth_service.security;

import com.landryokoye.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY;
//    private static final String SECRET_KEY = "GcZfX3X6ATe9viJkEQUVUUqU/f7vIBDYN2feR4rcWr1ZeE8RK9T9rbfEuVdo6IV2drjyCQDuUrANpUpdX8zZGA==";
    private final long EXP_TIME = 10 * 60 * 1000;

    public String generateToken(Authentication authentication) {
        log.debug("SecretKey: " + SECRET_KEY);

        User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("email", user.getEmail())
                .claim("role", authentication.getAuthorities())
                .claim("google_id", user.getGoogleId() == null ? null : user.getGoogleId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXP_TIME))
                .signWith(getKey(), Jwts.SIG.HS512)
                .compact();

    }

    public String generateRefreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .subject(authentication.getName())
                .claim("role", authentication.getAuthorities())
                .claim("user_id", user.getId())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXP_TIME))
                .signWith(getKey(), Jwts.SIG.HS512)
                .compact();

    }

    public String getUsernameFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }
    public String getEmailFromToken(String token){
        Claims claims= getAllClaim(token);
        return getClaims(token, claims.get("email", null));
    }

    private <T> T getClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claim = getAllClaim(token);
        return claimsResolver.apply(claim);
    }

    private Claims getAllClaim(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        final Date expiration = getClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}
