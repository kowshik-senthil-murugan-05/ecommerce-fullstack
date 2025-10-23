package com.project.ecommerce.appuser.address;

import com.project.ecommerce.exceptionhandler.APIException;
import com.project.ecommerce.util.PageDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressRepo addressRepo;

    public UserAddressDTO addAddress(long userId, UserAddressDTO dto) {
        UserAddress address = convertToEntity(dto);
        address.setUserId(userId);
        return convertToDTO(addressRepo.save(address));
    }

    public PageDetails<UserAddressDTO> fetchAllAddressesForUser(long userId, int pageNum, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);
        Page<UserAddress> userAddressPage = addressRepo.findAllByUserId(userId, pageDetails);

        return getUserAddressDTOPageDetails(userAddressPage);
    }

    private PageDetails<UserAddressDTO> getUserAddressDTOPageDetails(Page<UserAddress> userAddressPage)
    {
        List<UserAddress> userAddresses = userAddressPage.getContent();

        if(userAddresses.isEmpty())
        {
            throw new APIException("No address available for this user!!");
        }

        List<UserAddressDTO> userAddressDTOS = userAddresses.stream().map(this::convertToDTO).toList();

        return new PageDetails<>(
                userAddressDTOS,
                userAddressPage.getNumber(),
                userAddressPage.getSize(),
                userAddressPage.getTotalElements(),
                userAddressPage.getTotalPages(),
                userAddressPage.isLast()
        );
    }

    public UserAddressDTO fetchSpecificAddressForUser(long userId, long addressId) {
        UserAddress address = addressRepo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new APIException("Address not found"));

        return convertToDTO(address);
    }

    public UserAddressDTO updateAddress(long userId, UserAddressDTO dto) {
        UserAddress existing = addressRepo.findByIdAndUserId(dto.userAddressId, userId)
                .orElseThrow(() -> new APIException("Address not found"));

        existing.setBuildingNo(dto.buildingNum);
        existing.setBuildingName(dto.buildingName);
        existing.setStreetNo(dto.streetNum);
        existing.setStreetName(dto.streetName);
        existing.setCity(dto.city);
        existing.setPincode(dto.pincode);

        return convertToDTO(addressRepo.save(existing));
    }

    public void deleteAddress(long userId, long addressId) {
        UserAddress address = addressRepo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new APIException("Address not found"));
        addressRepo.delete(address);
    }

    private UserAddressDTO convertToDTO(UserAddress entity) {
        UserAddressDTO dto = new UserAddressDTO();
        dto.userAddressId = entity.getAddressId();
        dto.buildingNum = entity.getBuildingNo();
        dto.buildingName = entity.getBuildingName();
        dto.streetNum = entity.getStreetNo();
        dto.streetName = entity.getStreetName();
        dto.city = entity.getCity();
        dto.pincode = entity.getPincode();
        return dto;
    }

    private UserAddress convertToEntity(UserAddressDTO dto) {
        UserAddress entity = new UserAddress();
        entity.setBuildingNo(dto.buildingNum);
        entity.setBuildingName(dto.buildingName);
        entity.setStreetNo(dto.streetNum);
        entity.setStreetName(dto.streetName);
        entity.setCity(dto.city);
        entity.setPincode(dto.pincode);
        return entity;
    }
}

