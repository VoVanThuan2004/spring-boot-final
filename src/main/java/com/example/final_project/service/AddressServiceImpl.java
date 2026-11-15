package com.example.final_project.service;

import com.example.final_project.dto.request.AddressRequest;
import com.example.final_project.dto.response.AddressResponse;
import com.example.final_project.entity.Address;
import com.example.final_project.entity.User;
import com.example.final_project.exception.ResourceNotFoundException;
import com.example.final_project.mapper.AddressMapper;
import com.example.final_project.repository.AddressRepository;
import com.example.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(int userId) {
        getUser(userId);
        return addressMapper.mapToResponseList(addressRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public AddressResponse addAddress(int userId, AddressRequest request) {
        User user = getUser(userId);
        List<Address> addresses = addressRepository.findByUserId(userId);

        Address address = Address.builder()
                .user(user)
                .province(request.getProvince())
                .provinceCode(request.getProvinceCode())
                .district(request.getDistrict())
                .districtCode(request.getDistrictCode())
                .ward(request.getWard())
                .wardCode(request.getWardCode())
                .addressDetail(request.getAddressDetail())
                .isDefault(addresses.isEmpty())
                .build();

        return addressMapper.mapToResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(int userId, int addressId, AddressRequest request) {
        Address address = getOwnedAddress(userId, addressId);

        address.setProvince(request.getProvince());
        address.setProvinceCode(request.getProvinceCode());
        address.setDistrict(request.getDistrict());
        address.setDistrictCode(request.getDistrictCode());
        address.setWard(request.getWard());
        address.setWardCode(request.getWardCode());
        address.setAddressDetail(request.getAddressDetail());

        return addressMapper.mapToResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(int userId, int addressId) {
        Address target = getOwnedAddress(userId, addressId);

        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address address : addresses) {
            address.setDefault(address.getAddressId() == addressId);
        }
        addressRepository.saveAll(addresses);

        return addressMapper.mapToResponse(target);
    }

    @Override
    @Transactional
    public void deleteAddress(int userId, int addressId) {
        Address address = getOwnedAddress(userId, addressId);
        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);

        if (wasDefault) {
            addressRepository.findByUserId(userId).stream()
                    .filter(remaining -> remaining.getAddressId() != addressId)
                    .min(Comparator.comparingInt(Address::getAddressId))
                    .ifPresent(next -> {
                        next.setDefault(true);
                        addressRepository.save(next);
                    });
        }
    }

    private User getUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Address getOwnedAddress(int userId, int addressId) {
        return addressRepository.findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
    }
}
