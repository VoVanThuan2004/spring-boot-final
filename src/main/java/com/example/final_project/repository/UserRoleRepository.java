package com.example.final_project.repository;

import com.example.final_project.entity.Role;
import com.example.final_project.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByUserId(int userId);

    Optional<UserRole> findByRoleId(int roleId);
}
