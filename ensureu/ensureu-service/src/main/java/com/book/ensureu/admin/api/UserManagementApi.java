package com.book.ensureu.admin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.dto.RoleAssignmentDto;
import com.book.ensureu.admin.dto.UserManagementDto;
import com.book.ensureu.admin.service.UserManagementService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/user-management")
public class UserManagementApi {

    @Autowired
    private UserManagementService userManagementService;

    @CrossOrigin
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<UserManagementDto> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("getAllUsers page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return userManagementService.getAllUsers(pageable);
    }

    @CrossOrigin
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public UserManagementDto getUserById(@PathVariable("id") Long id) {
        log.info("getUserById id={}", id);
        return userManagementService.getUserById(id);
    }

    @CrossOrigin
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Page<UserManagementDto> searchUsers(
            @RequestParam(value = "q") String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("searchUsers q={} page={} size={}", searchTerm, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return userManagementService.searchUsers(searchTerm, pageable);
    }

    @CrossOrigin
    @RequestMapping(value = "/assign-role", method = RequestMethod.PUT)
    public ResponseEntity<UserManagementDto> assignRole(@RequestBody RoleAssignmentDto roleAssignmentDto) {
        log.info("assignRole userId={} role={}", roleAssignmentDto.getUserId(), roleAssignmentDto.getRoleType());
        UserManagementDto result = userManagementService.assignRole(roleAssignmentDto);
        return ResponseEntity.ok(result);
    }
}
