package com.project.ecommerce.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface OrderRepo extends JpaRepository<Order, Long>
{
    Page<Order> findAllByUserId(long userId, Pageable pageable);

    Page<Order> findByOrderDateBetween(OffsetDateTime start, OffsetDateTime end, Pageable pageable);

}
