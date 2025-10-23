package com.project.ecommerce.orderitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long>
{
    Page<OrderItem> findAllBySellerId(long sellerId, Pageable pageable);
}
