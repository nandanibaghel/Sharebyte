package com.sharebyte.dtos;

import com.sharebyte.enums.Role;
import com.sharebyte.enums.UserStatus;

public class AdminUserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    
    
	public AdminUserResponseDTO(Long id, String name, String email, Role role, UserStatus status) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.role = role;
		this.status = status;
	}
	
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}

    
}
