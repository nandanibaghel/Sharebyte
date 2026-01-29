package com.sharebyte.security;

import com.sharebyte.controllers.UserController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private long expiration;
		
	
	private SecretKeySpec getSigninKey() {
		return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
	}
	
	public String generateToken(String email) {
		return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
	}
	
	public String extractEmail(String token) {
	    return Jwts.parserBuilder()
	            .setSigningKey(getSigninKey())
	            .build()
	            .parseClaimsJws(token)
	            .getBody()
	            .getSubject();
	}

	public boolean validateToken(String token) {
	    try {
	        Jwts.parserBuilder()
	        .setSigningKey(getSigninKey())
	        .build()
	        .parseClaimsJws(token);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

}
