package com.project.ecommerce.cart;

import com.project.ecommerce.util.PageDetails;

public interface CartService
{
    UserCartDTO addProductToCart(long userId, AddToCartRequestDTO dto);

    PageDetails<UserCartDTO> getAllCarts(int pageNumber, int pageSize, String sortBy, String sortOrder);

    UserCartDTO getUserCartForUserId(long userId);

    UserCartDTO reduceUserCartItemQuantity(long userCartId, long productId);

    void removeCartItem(long userCartId, long productId);
}
