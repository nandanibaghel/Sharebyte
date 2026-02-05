package com.sharebyte.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import com.sharebyte.controllers.UserController;
import com.sharebyte.dtos.ChangePasswordRequestDTO;
import com.sharebyte.dtos.LoginRequestDTO;
import com.sharebyte.dtos.LoginResponseDTO;
import com.sharebyte.dtos.RegisterRequestDTO;
import com.sharebyte.dtos.RegisterResponseDTO;
import com.sharebyte.dtos.UpdateProfileRequestDTO;
import com.sharebyte.dtos.UserProfileResponseDTO;
import com.sharebyte.entities.User;
import com.sharebyte.entities.VerificationToken;
import com.sharebyte.enums.Role;
import com.sharebyte.enums.UserStatus;
import com.sharebyte.exception.AccountNotActiveException;
import com.sharebyte.exception.EmailAlreadyExistsException;
import com.sharebyte.exception.GlobalExceptionHandler;
import com.sharebyte.exception.UserNotFoundException;
import com.sharebyte.repositories.UserRepository;

import com.sharebyte.repositories.VerificationTokenRepository;
import com.sharebyte.security.JwtUtil;

@Service 
public class UserService {

    private GlobalExceptionHandler globalExceptionHandler;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder encoder;
  
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;

	@Autowired     
    private VerificationTokenRepository verificationRepo;

	public boolean changePassword(ChangePasswordRequestDTO dto) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User loggedInUser = userRepository.findByEmail(email);
		
		if(encoder.matches(dto.getOldPassword(), loggedInUser.getPassword())) {
			loggedInUser.setPassword(encoder.encode(dto.getNewPassword()));
			userRepository.save(loggedInUser);
			return true;
		}else {
			return false;
		}
	}
	
	public void updateProfile(
	        String email,
	        UpdateProfileRequestDTO dto,
	        MultipartFile profileImage) {

	    User user = userRepository.findByEmail(email);

	    if (dto != null && dto.getName() != null) {
	        user.setName(dto.getName());
	    }

	    if (profileImage != null && !profileImage.isEmpty()) {
	    		String ofn = profileImage.getOriginalFilename();
			String fname  = user.getId() + "_profile" + ofn.substring(ofn.lastIndexOf('.'));
			
			
	        	saveFile(profileImage, fname);
	        user.setProfileImage(fname);
	    }

	    userRepository.save(user);
	}

	
	public User getUserByEmail(String email){
		 return userRepository.findByEmail(email);
	} 
	
	public UserProfileResponseDTO getUserProfile(Long userId) {
		User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToProfileDTO(user);
	}
	
	public UserProfileResponseDTO getLoggedInUserProfile() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		User user = userRepository.findByEmail(email);
		
		if(user == null) {
			throw new UserNotFoundException("User not found");
		}
		return mapToProfileDTO(user);
	}
	
	private UserProfileResponseDTO mapToProfileDTO(User user) {
		UserProfileResponseDTO dto = new UserProfileResponseDTO();
		
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setRole(user.getRole().name());
		
		dto.setImage(user.getProfileImage());
		
		return dto;
	}
	
	private void saveFile(MultipartFile image, String fileName) {
		Path path = Paths.get("uploads/profile");
		Path filePath = path.resolve(fileName);
		try {
			Files.createDirectories(path);
			
			Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
	public void uploadUserImage(MultipartFile image) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		
		User user = userRepository.findByEmail(email);
		if(user == null) {
			throw new UserNotFoundException("User not found");
		}
		String ofn = image.getOriginalFilename();
		String fname  = user.getId() + "_profile" + ofn.substring(ofn.lastIndexOf('.'));
		
		
		saveFile(image, fname);
		
		
		user.setProfileImage(fname);
		

        userRepository.save(user);
	}
	
	public RegisterResponseDTO register(RegisterRequestDTO request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setStatus(UserStatus.ACTIVE);
		user.setRole(Role.valueOf(request.getRole())); 
	
		userRepository.save(user);
		
//		emailService.sendEmail(
//			    user.getEmail(),
//			    "Verify your ShareByte account",
//			    "Thank you for registering.\nPlease verify your email to activate your account."
//			);
//		
		String token = UUID.randomUUID().toString();

		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

		verificationRepo.save(verificationToken);
		
		String verificationLink = "http://localhost:8080/auth/verify?token=" + token;

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

	    if (!encoder.matches(
	            request.getPassword(),
	            user.getPassword())) {
	        throw new UserNotFoundException("Invalid credentials");
	    }

	    if (user.getStatus() != UserStatus.ACTIVE) {
	        throw new AccountNotActiveException(
	                "Please verify your email before login");
	    }
	    
	    String token = jwtUtil.generateToken(user.getEmail());

	    LoginResponseDTO response = new LoginResponseDTO();
	    response.setEmail(user.getEmail());
	    response.setRole(user.getRole());
	    response.setToken(token);
	    response.setMessage("Login successful");

	    return response;
	}

	public void verifyUser(String token) {
		System.out.println(verificationRepo);
	    Optional<VerificationToken> op =
	        verificationRepo.findByToken(token);
	    if(op.isEmpty() || !op.isPresent()) {
	    	throw new RuntimeException();
	    }
	    
	    VerificationToken verificationToken = op.get();
	      
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

