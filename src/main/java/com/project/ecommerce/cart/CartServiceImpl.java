package com.project.ecommerce.cart;

import com.project.ecommerce.cartitem.CartItem;
import com.project.ecommerce.cartitem.CartItemDTO;
import com.project.ecommerce.exceptionhandler.APIException;
import com.project.ecommerce.exceptionhandler.ResourceNotFoundException;
import com.project.ecommerce.product.ProductDTO;
import com.project.ecommerce.product.ProductService;
import com.project.ecommerce.util.PageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService
{

    private final UserCartRepo userCartRepo;
//    private final ProductClient productClient;
    private final ProductService productService;

    public CartServiceImpl (UserCartRepo userCartRepo, ProductService productService)
    {
        this.userCartRepo = userCartRepo;
        this.productService = productService;
    }

    public UserCartDTO addProductToCart(long userId, AddToCartRequestDTO dto)
    {
        ProductDTO product = productService.getProductObjForProductId(dto.productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", dto.productId));

        UserCart userCart = userCartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    UserCart newCart = new UserCart();
                    newCart.setUserId(userId);
                    return newCart;
                });

        Optional<CartItem> existingItem = userCart.getCartItems().stream()
                        .filter(item -> item.getProductId() == dto.productId)
                        .findFirst();

        if(existingItem.isPresent())
        {
            CartItem existingCartItem = existingItem.get();

            int newQty = dto.productQuantity + existingCartItem.getProductQuantity();
            existingCartItem.setProductQuantity(newQty);
            existingCartItem.setCurCartItemPrice(newQty * product.finalPrice);
        }
        else
        {
            CartItem newCartItem = createNewCartItem(dto, product, userCart);

            userCart.getCartItems().add(newCartItem);
        }

        userCart.setTotalPrice(calculateTotalCartPrice(userCart));

        UserCart savedCart = userCartRepo.save(userCart);

        return convertToDTO(savedCart);

        //Cascade = ALL → whenever you save UserCart, Hibernate will also save all CartItems inside it.
        //orphanRemoval = true → if you remove a CartItem from the list and save the UserCart, that cart item will be deleted from DB automatically.
        //So you don’t need to explicitly call cartItemRepo.save(cartItem) inside your else block.
        //Your userCartRepo.save(userCart) is enough — new cart items will also be persisted.
    }

    private CartItem createNewCartItem(AddToCartRequestDTO dto, ProductDTO productDTO, UserCart userCart)
    {
        CartItem cartItem = new CartItem();

        cartItem.setProductId(dto.productId);
        cartItem.setDescription(productDTO.description);
        cartItem.setProductName(productDTO.productName);
        cartItem.setProductImage(productDTO.imgUrl);
        cartItem.setProductQuantity(dto.productQuantity);
        cartItem.setProductPrice(productDTO.finalPrice);
        cartItem.setUserCart(userCart);
        cartItem.setCurCartItemPrice(dto.productQuantity * productDTO.finalPrice);

        return cartItem;
    }


    private double calculateTotalCartPrice(UserCart userCart)
    {
        return userCart.getCartItems().stream()
                .mapToDouble(CartItem::getCurCartItemPrice)
                .sum();
    }


//    private CartItem createCartItem(Product product, int productQuantity) {
//        CartItem userCartItem = new CartItem();
//
//        userCartItem.setProductId(product.getId());
//        userCartItem.setProductName(product.getProductName());
//        userCartItem.setProductImage(product.getProductImage());
//        userCartItem.setDescription(product.getDescription());
//        userCartItem.setProductQuantity(productQuantity);
//        userCartItem.setProductPrice(product.getFinalPrice());
//
//        double addedCartPrice = ((double) productQuantity * product.getFinalPrice());
//        userCartItem.setCurCartItemPrice(addedCartPrice);
//        return userCartItem;
//    }

    private UserCartDTO convertToDTO(UserCart userCart)
    {
        List<CartItemDTO> cartItemDTOS = userCart.getCartItems().stream()
                .map(product -> {
                    CartItemDTO dto = new CartItemDTO();

                    dto.cartId = userCart.getId();
                    dto.productId = product.getProductId();
                    dto.description = product.getDescription();
                    dto.productName = product.getProductName();
                    dto.productQuantity = product.getProductQuantity();
                    dto.productImage = product.getProductImage();

                    return dto;
                })
                .toList();

        UserCartDTO dto = new UserCartDTO();

        dto.cartId = userCart.getId();
        dto.cartItemDTOS = cartItemDTOS;
        dto.totalPrice = userCart.getTotalPrice();
        dto.userId = userCart.getUserId();

        return dto;
    }


    public PageDetails<UserCartDTO> getAllCarts(int pageNumber, int pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<UserCart> userCartPage = userCartRepo.findAll(pageDetails);

        if(userCartPage.isEmpty())
        {
            throw new APIException("No Existing carts!");
        }

        List<UserCartDTO> userCartDTOS = userCartPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return new PageDetails<>(
                userCartDTOS,
                userCartPage.getNumber(),
                userCartPage.getSize(),
                userCartPage.getTotalElements(),
                userCartPage.getTotalPages(),
                userCartPage.isLast()
        );
    }

    public UserCartDTO getUserCartForUserId(long userId)
    {
        UserCart userCart = userCartRepo.findByUserId(userId)
                .orElseThrow(() -> new APIException("No Cart Available for this user!"));

        return convertToDTO(userCart);
    }

    public UserCartDTO reduceUserCartItemQuantity(long userCartId, long productId)
    {
        UserCart cart = userCartRepo.findById(userCartId)
                .orElseThrow(() -> new ResourceNotFoundException("User cart", "userCartId", userCartId));

        cart.getCartItems().removeIf(
                (item) -> {
                    if(item.getProductId() == productId)
                    {
                        if(item.getProductQuantity() <= 1)
                        {
                            return true;
                        }
                        else
                        {
                            int newQty = item.getProductQuantity()-1;
                            item.setProductQuantity(newQty);
                            item.setCurCartItemPrice(newQty * item.getProductPrice());
                        }
                    }
                    return false;
                });

        cart.setTotalPrice(calculateTotalCartPrice(cart));

        return convertToDTO(userCartRepo.save(cart));
    }

    public void removeCartItem(long userCartId, long productId)
    {
        UserCart userCart = userCartRepo.findById(userCartId)
                .orElseThrow(() -> new ResourceNotFoundException("User cart", "userCart", userCartId));

        CartItem toRemove = userCart.getCartItems().stream()
                .filter((item) -> item.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item", "productId", productId));

        toRemove.setUserCart(null);

        userCart.setTotalPrice(calculateTotalCartPrice(userCart));

        if(userCart.getCartItems().isEmpty())
        {
            userCartRepo.deleteById(userCart.getId());
        }
        else
        {
            userCartRepo.save(userCart);
        }
    }


}
