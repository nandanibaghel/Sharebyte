package com.sharebyte.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws IOException,jakarta.servlet.ServletException {
		
		String header = request.getHeader("Authorization");
		
//		System.out.println(header);
		if(header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
//			System.out.println(token);
			
			if(jwtUtil.validateToken(token)) {
				String email = jwtUtil.extractEmail(token);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null , Collections.emptyList());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			}
		}
		
		filterChain.doFilter(request,response);
	}
}
