package com.project.ecommerce.appuser.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepo extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByRoleName(UserRole.Role roleName);

}
