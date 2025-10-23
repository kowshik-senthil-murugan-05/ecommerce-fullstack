package com.project.ecommerce.appuser;

import com.project.ecommerce.appuser.address.UserAddressDTO;
import com.project.ecommerce.appuser.role.UserRole;
import com.project.ecommerce.appuser.user.AppUser;
import com.project.ecommerce.appuser.user.AppUserDTO;
import com.project.ecommerce.appuser.user.AppUserRepo;
import com.project.ecommerce.exceptionhandler.APIException;
import com.project.ecommerce.util.PageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserServiceImpl implements AppUserService
{
    private final AppUserRepo appUserRepo;

    public AppUserServiceImpl(AppUserRepo appUserRepo)
    {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public PageDetails<AppUserDTO> getAllUsers(int pageNum, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);
        Page<AppUser> users = appUserRepo.findAll(pageDetails);

        if(users.isEmpty())
        {
            throw new APIException("No Users Available!");
        }

        List<AppUserDTO> userDTOS = users.stream().map(this::convertToDTO).toList();

        return new PageDetails<>(
                userDTOS,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isLast()
        );
    }

    private AppUserDTO convertToDTO(AppUser appUser)
    {
        AppUserDTO dto = new AppUserDTO();

        dto.userId = appUser.getId();
        dto.userName = appUser.getUserName();
        dto.email = appUser.getEmail();

        dto.userAddressDTOS = appUser.getAddresses().stream().map(addr ->
        {
            UserAddressDTO addressDTO = new UserAddressDTO();
            addressDTO.userAddressId = addr.getAddressId();
            addressDTO.buildingNum = addr.getBuildingNo();
            addressDTO.buildingName = addr.getBuildingName();
            addressDTO.streetNum = addr.getStreetNo();
            addressDTO.streetName = addr.getStreetName();
            addressDTO.buildingName = addr.getBuildingName();
            addressDTO.city = addr.getCity();
            addressDTO.pincode = addr.getPincode();

            return addressDTO;
        }).collect(Collectors.toList());

        dto.roleIds = appUser.getRoles().stream().map(UserRole::getRoleName)
                            .collect(Collectors.toList());

        return dto;
    }
}
