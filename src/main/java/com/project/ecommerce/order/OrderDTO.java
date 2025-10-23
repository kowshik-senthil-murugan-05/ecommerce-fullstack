package com.project.ecommerce.order;

import com.project.ecommerce.orderitem.OrderItemDTO;

import java.util.List;

public class OrderDTO {

    public long orderId;
    public Long userId;
    public List<OrderItemDTO> items;
    public double totalAmount;
}
