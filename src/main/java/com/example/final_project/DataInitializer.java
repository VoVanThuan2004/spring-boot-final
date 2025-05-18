package com.example.final_project;

import com.example.final_project.entity.Role;
import com.example.final_project.entity.User;
import com.example.final_project.entity.UserRole;
import com.example.final_project.repository.RoleRepository;
import com.example.final_project.repository.UserRepository;
import com.example.final_project.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // Tìm tài khoản admin
        Optional<User> existingUser = userRepository.findByFullName("admin");
        if (existingUser.isEmpty()) {
            // Nếu tài khoản admin chưa tồn tại, tạo mới tài khoản admin
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);


            User user = new User();
            user.setFullName("admin");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setActive(true);
            user.setEmail("admin@gmail.com");

            // Lưu user vào cơ sở dữ liệu
            user = userRepository.save(user);

            UserRole userRole = new UserRole();
            userRole.setRole(adminRole);
            userRole.setUser(user);

            // Lưu UserRole vào cơ sở dữ liệu
            userRoleRepository.save(userRole);

            System.out.println("Account admin initialized successfully");
        } else {
            System.out.println("Account admin already exists");
        }

    }
}
