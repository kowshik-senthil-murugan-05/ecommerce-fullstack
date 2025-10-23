package com.project.ecommerce.cart;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.ecommerce.cartitem.CartItem;
import com.project.ecommerce.util.AppUtil;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = UserCart.TABLE_NAME)
public class UserCart
{
    public static final String TABLE_NAME = AppUtil.TABLE_NAME_PREFIX + "user_cart";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private double totalPrice;

    @JsonManagedReference
    @OneToMany(mappedBy = "userCart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();


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

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    //    public void setUserCartItems(Map<Long, CartItem> userCartItems) {
//        this.userCartItems = userCartItems;
//    }
//
//    public Map<Long, CartItem> getUserCartItems() {
//        return userCartItems;
//    }

}
