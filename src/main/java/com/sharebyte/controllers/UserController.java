package com.sharebyte.controllers;

import java.security.Provider.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sharebyte.dtos.ChangePasswordRequestDTO;
import com.sharebyte.dtos.LoginRequestDTO;
import com.sharebyte.dtos.LoginResponseDTO;
import com.sharebyte.dtos.RegisterRequestDTO;
import com.sharebyte.dtos.RegisterResponseDTO;
import com.sharebyte.dtos.UpdateProfileRequestDTO;
import com.sharebyte.dtos.UserProfileResponseDTO;
import com.sharebyte.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request){
		boolean flag = userService.changePassword(request);
		
		if(flag) {
			return ResponseEntity.ok("Password updated successfully");
		}else {
			return new ResponseEntity<>("Old password is missmatch..",HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/profile")
	public ResponseEntity<?> updateProfile(
	        @RequestPart(required = false) UpdateProfileRequestDTO dto,
	        @RequestPart(required = false) MultipartFile profileImage, 
	        Authentication authentication) {

	    userService.updateProfile(authentication.getName(), dto, profileImage);
	    return ResponseEntity.ok("Profile updated successfully");
	}
	
	@GetMapping("/{userId}/profile")
	public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getUserProfile(userId));
	}
	
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getLoggedInUserProfile());
    }
	
	@PostMapping("/upload-image")
	public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {

	        userService.uploadUserImage(image);
	        return ResponseEntity.ok("Image uploaded successfully");
	}
	
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
