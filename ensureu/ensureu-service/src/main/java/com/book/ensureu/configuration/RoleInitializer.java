package com.book.ensureu.configuration;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.book.ensureu.constant.RoleType;
import com.book.ensureu.model.Role;
import com.book.ensureu.repository.RolesRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public void run(String... args) {
        initRoleIfAbsent("ROLE_USER", RoleType.USER);
        initRoleIfAbsent("ROLE_ADMIN", RoleType.ADMIN);
        initRoleIfAbsent("ROLE_SUPERADMIN", RoleType.SUPERADMIN);
        initRoleIfAbsent("ROLE_TEACHER", RoleType.TEACHER);
    }

    private void initRoleIfAbsent(String id, RoleType roleType) {
        Optional<Role> roleOpt = rolesRepository.findById(id);
        if (!roleOpt.isPresent()) {
            Role role = new Role();
            role.setId(id);
            role.setRoleType(roleType);
            rolesRepository.save(role);
            log.info("Initialized role: {} ({})", id, roleType);
        }
    }
}
