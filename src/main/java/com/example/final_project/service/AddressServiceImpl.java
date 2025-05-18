package com.example.final_project.service;

import com.example.final_project.dto.AddressDTO;
import com.example.final_project.entity.Address;
import com.example.final_project.entity.User;
import com.example.final_project.repository.AddressRepository;
import com.example.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public ResponseEntity<?> addAddress(int userId, AddressDTO addressDTO) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Address> addresses = addressRepository.findByUserId(userId);

        Address address = new Address();
        address.setUser(user.get());
        address.setWard(addressDTO.getWard());
        address.setWardCode(addressDTO.getWardCode());
        address.setDistrict(addressDTO.getDistrict());
        address.setDistrictCode(addressDTO.getDistrictCode());
        address.setProvince(addressDTO.getProvince());
        address.setProvinceCode(addressDTO.getProvinceCode());
        address.setAddressDetail(addressDTO.getAddressDetail());
        address.setDefault(addresses.isEmpty());


        addressRepository.save(address);

        return ResponseEntity.ok().body("Address added");
    }

    @Override
    public ResponseEntity<?> updateDefaultAddress(int userId, int addressId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address address : addresses) {
            address.setDefault(address.getAddress_id() == addressId);
        }

        addressRepository.saveAll(addresses);

        return ResponseEntity.ok().body("Address updated default");
    }

    @Override
    public ResponseEntity<?> deleteAddress(int addressId) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isEmpty()) {
            return ResponseEntity.badRequest().body("Address not found");
        }

        addressRepository.delete(address.get());

        return ResponseEntity.ok().body("Address deleted");
    }
}
