package com.book.ensureu.admin.service.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.dto.RoleAssignmentDto;
import com.book.ensureu.admin.dto.UserManagementDto;
import com.book.ensureu.admin.service.UserManagementService;
import com.book.ensureu.constant.RoleType;
import com.book.ensureu.model.Role;
import com.book.ensureu.model.User;
import com.book.ensureu.repository.RolesRepository;
import com.book.ensureu.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Page<UserManagementDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::toDto);
    }

    @Override
    public UserManagementDto getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return toDto(userOpt.get());
    }

    @Override
    public Page<UserManagementDto> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return users.map(this::toDto);
    }

    @Override
    public UserManagementDto assignRole(RoleAssignmentDto roleAssignmentDto) {
        Optional<User> userOpt = userRepository.findById(roleAssignmentDto.getUserId());
        if (!userOpt.isPresent()) {
            throw new NoSuchElementException("User not found with id: " + roleAssignmentDto.getUserId());
        }

        User user = userOpt.get();
        RoleType targetRoleType = roleAssignmentDto.getRoleType();

        // Find or create the Role document for the target role type
        String roleId = "ROLE_" + targetRoleType.name();
        Role role;
        Optional<Role> roleOpt = rolesRepository.findById(roleId);
        if (roleOpt.isPresent()) {
            role = roleOpt.get();
        } else {
            role = new Role();
            role.setId(roleId);
            role.setRoleType(targetRoleType);
            rolesRepository.save(role);
        }

        // Replace user's roles with the single new role
        user.setRoles(Collections.singletonList(role));
        user.setModifiedDate(System.currentTimeMillis());
        userRepository.save(user);

        log.info("Assigned role {} to user {}", targetRoleType, user.getUserName());
        return toDto(user);
    }

    private UserManagementDto toDto(User user) {
        List<RoleType> roleTypes = new ArrayList<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getRoleType() != null) {
                    roleTypes.add(role.getRoleType());
                }
            }
        }
        return UserManagementDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .emailId(user.getEmailId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .mobileNumber(user.getMobileNumber())
                .roleTypes(roleTypes)
                .createDate(user.getCreateDate())
                .modifiedDate(user.getModifiedDate())
                .build();
    }
}
