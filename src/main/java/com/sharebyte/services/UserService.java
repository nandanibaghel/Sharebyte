package com.sharebyte.services;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sharebyte.controllers.UserController;
import com.sharebyte.dtos.LoginRequestDTO;
import com.sharebyte.dtos.LoginResponseDTO;
import com.sharebyte.dtos.RegisterRequestDTO;
import com.sharebyte.dtos.RegisterResponseDTO;
import com.sharebyte.entities.User;
import com.sharebyte.entities.VerificationToken;
import com.sharebyte.enums.UserStatus;
import com.sharebyte.exception.AccountNotActiveException;
import com.sharebyte.exception.EmailAlreadyExistsException;
import com.sharebyte.exception.GlobalExceptionHandler;
import com.sharebyte.exception.UserNotFoundException;
import com.sharebyte.repositories.UserRepository;
import com.sharebyte.repositories.VerificationRepo;

@Service 
public class UserService {

    private GlobalExceptionHandler globalExceptionHandler;
  
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EmailService emailService;

	@Autowired     
    VerificationRepo verificationRepo;

	public User getUserByEmail(String email){
		 return userRepository.findByEmail(email);
	} 
	
	public RegisterResponseDTO register(RegisterRequestDTO request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setStatus(UserStatus.PENDING_VERIFIACATION);
		user.setRole(request.getRole());
		
		userRepository.save(user);
		
		emailService.sendEmail(
			    user.getEmail(),
			    "Verify your ShareByte account",
			    "Thank you for registering.\nPlease verify your email to activate your account."
			);
		
		String token = UUID.randomUUID().toString();

		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

		verificationRepo.save(verificationToken);
		
		String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;

		emailService.sendEmail(
			    user.getEmail(),
			    "Verify your ShareByte account",
			    "Click the link to verify your account:\n" + verificationLink
		);

		RegisterResponseDTO response = new RegisterResponseDTO();
		response.setEmail(user.getEmail());
		response.setStatus(user.getStatus().name());
		response.setMessage("Registration successful. Please verify your email to activate your account.");
		
		return response;
	}
	
	
	public LoginResponseDTO login(LoginRequestDTO request) {

	    User user = userRepository.findByEmail(request.getEmail());
        if(user==null) {
        	throw new UserNotFoundException("Invalid credentials");
        }

//	    if (!passwordEncoder.matches(
//	            request.getPassword(),
//	            user.getPassword())) {
//	        throw new UserNotFoundException("Invalid credentials");
//	    }

	    if (user.getStatus() != UserStatus.ACTIVE) {
	        throw new AccountNotActiveException(
	                "Please verify your email before login");
	    }

	    LoginResponseDTO response = new LoginResponseDTO();
	    response.setEmail(user.getEmail());
	    response.setRole(user.getRole());
	    response.setMessage("Login successful");

	    return response;
	}

	public void verifyUser(String token) {

	    VerificationToken verificationToken =
	        verificationRepo.findByToken(token)
	        .orElseThrow(() ->
	            new RuntimeException("Invalid verification token"));

	    if (verificationToken.getExpiryDate()
	            .isBefore(LocalDateTime.now())) {
	        throw new RuntimeException("Token expired");
	    }

	    User user = verificationToken.getUser();
	    user.setStatus(UserStatus.ACTIVE);

	    userRepository.save(user);
	    verificationRepo.delete(verificationToken);
	}
	
}

