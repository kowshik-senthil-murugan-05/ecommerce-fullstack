package com.project.ecommerce.restcontroller;

import com.project.ecommerce.appuser.address.UserAddressDTO;
import com.project.ecommerce.appuser.address.UserAddressService;
import com.project.ecommerce.cart.AddToCartRequestDTO;
import com.project.ecommerce.cart.CartService;
import com.project.ecommerce.cart.UserCartDTO;
import com.project.ecommerce.exceptionhandler.APIResponse;
import com.project.ecommerce.order.OrderDTO;
import com.project.ecommerce.order.OrderService;
import com.project.ecommerce.payment.PaymentDTO;
import com.project.ecommerce.payment.PaymentInputDTO;
import com.project.ecommerce.payment.PaymentService;
import com.project.ecommerce.product.ProductDTO;
import com.project.ecommerce.product.ProductService;
import com.project.ecommerce.util.AppUtil;
import com.project.ecommerce.util.PageDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@PreAuthorize("hasRole('USER', 'ADMIN')")
public class UserController
{
    private final ProductService productService;
    private final UserAddressService userAddressService;
    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentService paymentService;
    public UserController(ProductService productService, UserAddressService userAddressService,
                          OrderService orderService, CartService cartService, PaymentService paymentService)
    {
        this.productService = productService;
        this.userAddressService = userAddressService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<PageDetails<ProductDTO>> fetchAllProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/fetch/{productId}")
    public ResponseEntity<ProductDTO> fetchProduct(@PathVariable long productId) {
        return ResponseEntity.ok(productService.getProductObjForProductId(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<PageDetails<ProductDTO>> searchProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        return ResponseEntity.ok(productService.searchProducts(name, category, pageNumber, pageSize, sortOrder, sortBy));
    }

    @PostMapping("/address/add/{userId}")
    public ResponseEntity<APIResponse<UserAddressDTO>> addAddress(@PathVariable long userId, @RequestBody UserAddressDTO addressDTO)
    {
        UserAddressDTO addedAddress = userAddressService.addAddress(userId, addressDTO);

        return new ResponseEntity<>(
                new APIResponse<>("Address for user added!", true, addedAddress),
                HttpStatus.OK
        );
    }

    @PutMapping("/address/update/{userId}")
    public ResponseEntity<APIResponse<UserAddressDTO>> updateAddress(@PathVariable long userId, @RequestBody UserAddressDTO addressDTO)
    {
        UserAddressDTO updatedAddress = userAddressService.updateAddress(userId, addressDTO);

        return new ResponseEntity<>(
                new APIResponse<>("User address updated!", true, updatedAddress),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/address/delete/{userId}/{addressId}")
    public ResponseEntity<APIResponse<Void>> deleteAddress(@PathVariable long userId, @PathVariable long addressId)
    {
        userAddressService.deleteAddress(userId, addressId);

        return new ResponseEntity<>(
                new APIResponse<>("User address deleted!", true, null),
                HttpStatus.OK
        );
    }

    @GetMapping("/fetch/specificAddress/forUser/{userId}/{addressId}")
    public ResponseEntity<UserAddressDTO> fetchSpecificAddressForUser(@PathVariable long userId, @PathVariable long addressId) {
        UserAddressDTO specificAddressForUser = userAddressService.fetchSpecificAddressForUser(userId, addressId);

        return new ResponseEntity<>(
                specificAddressForUser,
                HttpStatus.OK
        );
    }

    @GetMapping("/fetch/allAddresses/forUser/{userId}")
    public ResponseEntity<PageDetails<UserAddressDTO>> fetchAllAddressesForUser(
            @PathVariable long userId,
            @RequestParam(name = "pageNum", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNum,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder)
    {
        PageDetails<UserAddressDTO> fetchedAllAddressesForUser = userAddressService.fetchAllAddressesForUser(userId, pageNum, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(
                fetchedAllAddressesForUser,
                HttpStatus.OK
        );
    }

    // ✅ Place order
    @PostMapping("/place")
    public ResponseEntity<APIResponse<OrderDTO>> placeOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO placedOrder = orderService.placeOrder(orderDTO);
        return new ResponseEntity<>(
                new APIResponse<>("Order placed successfully!", true, placedOrder),
                HttpStatus.CREATED
        );
    }

    //Get all orders of logged-in user
    @GetMapping("/myOrders/{userId}")
    public ResponseEntity<PageDetails<OrderDTO>> getMyOrders(
            @PathVariable long userId,
            @RequestParam(value = "pageNumber", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder) {

        PageDetails<OrderDTO> myOrders = orderService.fetchOrdersForUser(userId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(myOrders, HttpStatus.OK);
    }

    @PostMapping("/product/addToCart/{userId}")
    public ResponseEntity<APIResponse<UserCartDTO>> addProductToCart(
            @PathVariable long userId, @RequestBody AddToCartRequestDTO requestDTO)
    {
        /*
         *   Adding products to cart is tested. It is working fine.
         *   todo - Adding same product again results in creation of two rows, it should be added in same row.
         */

        UserCartDTO dto = cartService.addProductToCart(userId, requestDTO);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Product added to cart!!",
                        true,
                        dto),
                HttpStatus.CREATED
        );
    }//

    @GetMapping("/userCart/get/{userId}")
    public ResponseEntity<UserCartDTO> getCartForUserId(@PathVariable long userId){
        UserCartDTO cartForUserId = cartService.getUserCartForUserId(userId);

        return new ResponseEntity<>(
                cartForUserId,
                HttpStatus.OK
        );
    }

    @PostMapping("/cartItem/remove/{userCartId}/{productId}")
    public ResponseEntity<APIResponse<Void>> removeCartItem(@PathVariable long userCartId, @PathVariable long productId)
    {
        cartService.removeCartItem(userCartId, productId);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Cart item removed successfully!!",
                        true, null),
                HttpStatus.OK
        );
    }

    @PostMapping("/cartItem/quantity/reduce/{userCartId}/{productId}")
    public ResponseEntity<UserCartDTO> reduceCartItemQuantity(@PathVariable long userCartId, @PathVariable long productId)
    {
        UserCartDTO reducedUserCartItemQuantity = cartService.reduceUserCartItemQuantity(userCartId, productId);

        return new ResponseEntity<UserCartDTO>(
                reducedUserCartItemQuantity,
                HttpStatus.OK
        );
    }

    @PostMapping("/make")
    public ResponseEntity<APIResponse<PaymentDTO>> makePayment(@RequestBody PaymentInputDTO dto) {
        PaymentDTO paymentDTO = paymentService.makePayment(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Payment done!!",
                        true,
                        paymentDTO
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/get/forOrder/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentForOrder(@PathVariable long orderId) {
        PaymentDTO paymentForOrder = paymentService.getPaymentDetails(orderId);

        return new ResponseEntity<>(
                paymentForOrder,
                HttpStatus.OK
        );
    }
}
