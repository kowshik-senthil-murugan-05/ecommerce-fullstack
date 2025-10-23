package com.project.ecommerce.appuser.address;

import com.project.ecommerce.util.PageDetails;

public interface UserAddressService
{
    UserAddressDTO addAddress(long userId, UserAddressDTO dto);

    UserAddressDTO fetchSpecificAddressForUser(long userId, long addressId);

    PageDetails<UserAddressDTO> fetchAllAddressesForUser(long userId, int pageNum, int pageSize, String sortBy, String sortOrder);

    UserAddressDTO updateAddress(long userId, UserAddressDTO dto);

    void deleteAddress(long userId, long addressId);
}
