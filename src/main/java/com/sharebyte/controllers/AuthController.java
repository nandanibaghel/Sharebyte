package com.sharebyte.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sharebyte.services.UserService;

public class AuthController {
	@GetMapping("/verify")
	public ResponseEntity<String> verifyAccount(
	        @RequestParam String token) {

	    UserService userService = new UserService();
		userService.verifyUser(token);
	    return ResponseEntity.ok("Account verified successfully");
	}
}
