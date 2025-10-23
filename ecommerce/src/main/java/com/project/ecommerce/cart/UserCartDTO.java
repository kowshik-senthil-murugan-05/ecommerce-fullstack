package com.project.ecommerce.cart;


import com.project.ecommerce.cartitem.CartItemDTO;

import java.util.List;

public class UserCartDTO
{
    public long cartId;
    public long userId;
    public List<CartItemDTO> cartItemDTOS;
    public double totalPrice;
}
