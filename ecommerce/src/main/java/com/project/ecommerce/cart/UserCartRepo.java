package com.project.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCartRepo extends JpaRepository<UserCart, Long>
{
    Optional<UserCart> findByUserId(long userId);

}
