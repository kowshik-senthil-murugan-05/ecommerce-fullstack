package com.project.ecommerce.order;

import com.project.ecommerce.orderitem.OrderItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ecomm_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;
    private LocalDateTime orderDate;
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /*
    Links the OrderItem to its parent Order via mappedBy.
    Tells JPA to cascade all persistence operations, so when you save the Order, it also automatically
    saves all its items.
    */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    //todo address of order, payment of order

    public enum OrderStatus {
        PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
    }

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}


