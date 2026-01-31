package com.sharebyte.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sharebyte.config.SecurityConfig;
import com.sharebyte.repositories.VerificationTokenRepository;
import com.sharebyte.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SecurityConfig securityConfig;

    private final VerificationTokenRepository verificationTokenRepository;
	@Autowired
	private UserService userService;

    AuthController(VerificationTokenRepository verificationTokenRepository, SecurityConfig securityConfig) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.securityConfig = securityConfig;
    }
	@GetMapping("/verify")
	public ResponseEntity<String> verifyAccount(
	        @RequestParam String token) {

	    
		userService.verifyUser(token);
	    return ResponseEntity.ok("Account verified successfully");
	}
	
	@GetMapping("/test")
	public ResponseEntity<String> test () {
		return new ResponseEntity<String>("test", HttpStatus.OK);
	}

}
