package com.project.ecommerce.appuser.address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepo extends JpaRepository<UserAddress, Long>
{
    Page<UserAddress> findAllByUserId(long userId, Pageable pageDetails);

    Optional<UserAddress> findByIdAndUserId(long addressId, long userId);
}
