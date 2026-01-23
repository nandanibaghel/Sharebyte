package com.sharebyte.services;

import java.util.HashMap;
import java.util.Map;

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
import com.sharebyte.enums.UserStatus;
import com.sharebyte.exception.AccountNotActiveException;
import com.sharebyte.exception.EmailAlreadyExistsException;
import com.sharebyte.exception.GlobalExceptionHandler;
import com.sharebyte.exception.UserNotFoundException;
import com.sharebyte.repositories.UserRepository;

@Service 
public class UserService {

    private final GlobalExceptionHandler globalExceptionHandler;
  
	@Autowired
	UserRepository userRepository;

    UserService(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }

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


}
