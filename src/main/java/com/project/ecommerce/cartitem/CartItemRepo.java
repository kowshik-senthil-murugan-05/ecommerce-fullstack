package com.project.ecommerce.cartitem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
}
