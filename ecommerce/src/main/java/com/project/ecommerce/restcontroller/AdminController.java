package com.project.ecommerce.restcontroller;

import com.project.ecommerce.category.CategoryDTO;
import com.project.ecommerce.category.CategoryService;
import com.project.ecommerce.exceptionhandler.APIResponse;
import com.project.ecommerce.order.OrderDTO;
import com.project.ecommerce.order.OrderRepo;
import com.project.ecommerce.order.OrderService;
import com.project.ecommerce.order.OrderServiceImpl;
import com.project.ecommerce.order.OrderServiceImpl.OrderStatusUpdateDTO;
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
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(CategoryService categoryService, ProductService productService,
                           OrderService orderService)
    {
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
    }


    // --- Category Management ---
    @PostMapping("/category/add")
    public ResponseEntity<APIResponse<CategoryDTO>> addCategory(@Valid @RequestBody CategoryDTO dto)
    {
        CategoryDTO categoryDTO = categoryService.addCategory(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Category created successfully!!",
                        true,
                        categoryDTO),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/categories/listAll")
    public ResponseEntity<PageDetails<CategoryDTO>> listAllCategories
            (@RequestParam(name = "pageNum", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNum,
             @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
             @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
             @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder)
    {
        PageDetails<CategoryDTO> categoryDTOPageDetails = categoryService.listAllCategories(pageNum, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(
                categoryDTOPageDetails,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/category/delete/{categoryId}")
    public ResponseEntity<APIResponse<Void>> removeCategory(@PathVariable long categoryId)
    {
        categoryService.removeCategory(categoryId);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Category with category id : " + categoryId + " removed successfully!!",
                        true, null
                ),
                HttpStatus.OK
        );
    }

    @PutMapping("/category/update")
    public ResponseEntity<APIResponse<CategoryDTO>> updateCategory(@RequestBody CategoryDTO dto)
    {
        CategoryDTO updatedCategory = categoryService.updateCategory(dto);

        return new ResponseEntity<>(
                new APIResponse<>(
                        "Category updated!",
                        true,
                        updatedCategory),
                HttpStatus.OK
        );
    }

    //todo Get category by ID

    // --- Product Management ---
    @GetMapping("/products/listAll")
    public ResponseEntity<PageDetails<ProductDTO>> listAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder)
    {
        PageDetails<ProductDTO> allProducts = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(
                allProducts,
                HttpStatus.OK
        );
    }

    @GetMapping("/products/listAll/byCategory/{categoryId}")
    public ResponseEntity<PageDetails<ProductDTO>> listAllProductsForCategory(
            @RequestParam(value = "pageNumber", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder,
            @PathVariable long categoryId)
    {
        PageDetails<ProductDTO> allProductsForCategoryId = productService.getAllProductsForCategoryId(pageNumber, pageSize, sortBy, sortOrder, categoryId);

        return new ResponseEntity<>(
                allProductsForCategoryId,
                HttpStatus.OK
        );
    }

    // ✅ Update order status
    @PutMapping("/update-status")
    public ResponseEntity<APIResponse<OrderDTO>> updateOrderStatus(@RequestBody OrderStatusUpdateDTO statusUpdateDTO) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(statusUpdateDTO);
        return new ResponseEntity<>(
                new APIResponse<>("Order status updated!", true, updatedOrder),
                HttpStatus.OK
        );
    }

    // ✅ List orders by month
    @GetMapping("/list/{year}/{month}")
    public ResponseEntity<PageDetails<OrderDTO>> listOrdersForMonth(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(value = "pageNumber", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder) {

        PageDetails<OrderDTO> orders = orderService.listOrdersForMonth(year, month, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // ✅ Delete order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<APIResponse<OrderDTO>> deleteOrder(@PathVariable long orderId) {
        OrderDTO orderDTO = orderService.deleteOrder(orderId);
        return new ResponseEntity<>(
                new APIResponse<>("Order deleted!", true, orderDTO),
                HttpStatus.OK
        );
    }


    
}
