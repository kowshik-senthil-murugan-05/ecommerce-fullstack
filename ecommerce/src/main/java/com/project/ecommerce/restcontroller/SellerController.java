package com.project.ecommerce.restcontroller;

import com.project.ecommerce.exceptionhandler.APIResponse;
import com.project.ecommerce.order.OrderDTO;
import com.project.ecommerce.order.OrderService;
import com.project.ecommerce.order.OrderServiceImpl.OrderStatusUpdateDTO;
import com.project.ecommerce.orderitem.OrderItemDTO;
import com.project.ecommerce.product.ProductDTO;
import com.project.ecommerce.product.ProductService;
import com.project.ecommerce.util.AppUtil;
import com.project.ecommerce.util.PageDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerController {

    private final ProductService productService;
    private final OrderService orderService;

    public SellerController(ProductService productService, OrderService orderService)
    {
        this.productService = productService;
        this.orderService = orderService;
    }

    // --- Seller Product APIs ---
    @PostMapping("/product/add")
    public ResponseEntity<APIResponse<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO dto)
    {
        ProductDTO savedProduct = productService.saveProduct(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Product created successfully",
                        true,
                        savedProduct),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<ProductDTO>> updateProduct(@RequestBody ProductDTO dto)
    {
        productService.updateProduct(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Product with product id : " + dto.id + " updated successfully!!",
                        true, null),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<APIResponse<ProductDTO>> deleteProduct(@PathVariable long productId)
    {
        productService.deleteProduct(productId);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Product with product id : " + productId + " deleted successfully!!",
                        true, null),
                HttpStatus.OK
        );
    }

    @GetMapping("/fetch/products/forSeller/{sellerId}")
    public ResponseEntity<PageDetails<ProductDTO>> fetchProductsBySeller(
            @PathVariable long sellerId,
            @RequestParam(value = "pageNumber", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder)
    {
        PageDetails<ProductDTO> products = productService.fetchProductsBySeller(sellerId, pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(
                products,
                HttpStatus.OK
        );
    }

    // ✅ Fetch all order items for a seller
    @GetMapping("/list/{sellerId}")
    public ResponseEntity<PageDetails<OrderItemDTO>> fetchOrdersForSeller(
            @PathVariable long sellerId,
            @RequestParam(value = "pageNumber", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder) {

        PageDetails<OrderItemDTO> pageDetails =
                orderService.fetchOrdersForSeller(sellerId, pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(pageDetails, HttpStatus.OK);
    }

    // Seller can update item status (ex: mark as shipped)
    @PutMapping("/update-status")
    public ResponseEntity<APIResponse<OrderDTO>> updateOrderItemStatus(
            @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO) {

        OrderDTO updatedItem = orderService.updateOrderStatus(orderStatusUpdateDTO);

        return new ResponseEntity<>(
                new APIResponse<>("Order item status updated!", true, updatedItem),
                HttpStatus.OK
        );
    }
}

