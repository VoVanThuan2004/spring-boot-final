package com.example.final_project.repository;

import com.example.final_project.entity.Order;
import com.example.final_project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByFullName(String fullName);
    Optional<User> findByEmail(String email);

    Page<User> findByIdNot(int id, Pageable pageable);
}
