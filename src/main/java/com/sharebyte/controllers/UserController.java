package com.sharebyte.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharebyte.dtos.LoginRequestDTO;
import com.sharebyte.dtos.LoginResponseDTO;
import com.sharebyte.dtos.RegisterRequestDTO;
import com.sharebyte.dtos.RegisterResponseDTO;
import com.sharebyte.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/pro")
	public ResponseEntity<String> profile(){
		System.out.print("profile");
		return new ResponseEntity("profile",HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
		RegisterResponseDTO response =  userService.register(request);
		
		 return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(
	        @Valid @RequestBody LoginRequestDTO request) {

	    LoginResponseDTO response = userService.login(request);

	    return ResponseEntity.ok(response);
	}
}
