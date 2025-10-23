package com.project.ecommerce.orderitem;

public class OrderItemDTO {
    public long orderItemId; //for sending response to client/view
    public long productId;
    public int quantity;
    public double price;
    public long sellerId;
}
