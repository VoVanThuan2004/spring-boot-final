package com.example.final_project.repository;

import com.example.final_project.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(int userId);

    Optional<Address> findByAddressIdAndUserId(int addressId, int userId);
}
