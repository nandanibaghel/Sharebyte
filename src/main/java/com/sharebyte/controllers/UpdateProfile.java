package com.sharebyte.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.sharebyte.dtos.UpdateProfileRequestDTO;
import com.sharebyte.services.UserService;

public class UpdateProfile {
	@Autowired
	UserService  userService;
	
	
	
}
