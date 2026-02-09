package com.book.ensureu.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.admin.dto.RoleAssignmentDto;
import com.book.ensureu.admin.dto.UserManagementDto;

public interface UserManagementService {

    Page<UserManagementDto> getAllUsers(Pageable pageable);

    UserManagementDto getUserById(Long id);

    Page<UserManagementDto> searchUsers(String searchTerm, Pageable pageable);

    UserManagementDto assignRole(RoleAssignmentDto roleAssignmentDto);
}
