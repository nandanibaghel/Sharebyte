package com.sharebyte.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sharebyte.dtos.AdminUserResponseDTO;
import com.sharebyte.entities.User;
import com.sharebyte.enums.Role;
import com.sharebyte.enums.UserStatus;
import com.sharebyte.repositories.UserRepository;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    public Page<AdminUserResponseDTO> getUsers(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String role,
            String status
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users;

        if (role != null && status != null) {
            users = userRepository.findByRoleAndStatus(Role.valueOf(role), UserStatus.valueOf(status), pageable);
        } else if (role != null) {
            users = userRepository.findByRole(Role.valueOf(role), pageable);
        } else if (status != null) {
            users = userRepository.findByStatus(UserStatus.valueOf(status), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(user ->
                new AdminUserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus()
                )
        );
    }
}

