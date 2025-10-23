package com.project.ecommerce.product;

import com.project.ecommerce.category.Category;
import com.project.ecommerce.category.CategoryService;
import com.project.ecommerce.exceptionhandler.APIException;
import com.project.ecommerce.exceptionhandler.ResourceNotFoundException;
import com.project.ecommerce.util.PageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService
{

    private final ProductRepo productRepo;

    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepo productRepo, CategoryService categoryService)
    {
        this.productRepo = productRepo;
        this.categoryService = categoryService;
    }

    public ProductDTO saveProduct(ProductDTO dto)
    {
        Category category = categoryService.getCategoryObj(dto.categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", dto.categoryId));

        Product productObj = productRepo.findByCategoryIdAndProductName(dto.categoryId, dto.productName);

        if(productObj != null)
        {
            throw new APIException("Product already exists!");
        }

        Product savedProduct = productRepo.save(buildProductFromDTO(dto, category));

        return convertToDTO(savedProduct);
    }

    private Product buildProductFromDTO(ProductDTO dto, Category category)
    {
        Product product = new Product();

        product.setProductName(dto.productName);
        product.setDescription(dto.description);
        product.setCategory(category);
        product.setProductPrice(dto.productPrice);
        product.setProductImage("default.png");
        product.setDiscount(dto.discount);
        double finalPrice = calculationOfFinalPrice(dto.productPrice, dto.discount);
        product.setFinalPrice(finalPrice);
        product.setAvailableProductQuantity(dto.totalQuantity);

        return product;
    }

    public PageDetails<ProductDTO> getAllProducts(int pageNum, int pageSize, String sortBy, String sortOrder)
    {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepo.findAll(pageDetails);

        if(productPage.isEmpty())
        {
            throw new APIException("No Products created!");
        }

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return new PageDetails<>(
                productDTOS,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public PageDetails<ProductDTO> getAllProductsForCategoryId(int pageNum, int pageSize, String sortBy, String sortOrder, long categoryId)
    {
        if(!categoryService.isValidCategory(categoryId))
        {
            throw new APIException("Category not exists!!");
        }

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepo.findAllByCategoryId(categoryId, pageDetails);

        if(productPage.isEmpty())
        {
            throw new APIException("No products available for this category!");
        }

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return new PageDetails<>(
                productDTOS,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }


    public ProductDTO updateProduct(ProductDTO dto)
    {
        Product product = productRepo.findById(dto.id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "productId", dto.id
                ));

        Optional.ofNullable(dto.imgUrl).ifPresent(product::setProductImage);
        Optional.ofNullable(dto.productName).ifPresent(product::setProductName);
        Optional.ofNullable(dto.description).ifPresent(product::setDescription);

        if(dto.productPrice != product.getProductPrice() || dto.discount != product.getDiscount())
        {
            product.setProductPrice(dto.productPrice);
            product.setDiscount(dto.discount);
            double finalPrice = calculationOfFinalPrice(dto.productPrice, dto.discount);
            product.setFinalPrice(finalPrice);
        }

        if(dto.totalQuantity != product.getAvailableProductQuantity())
        {
            product.setAvailableProductQuantity(dto.totalQuantity);
        }

        return convertToDTO(productRepo.save(product));
    }

    private double calculationOfFinalPrice(double productPrice, double discount)
    {
        return productPrice - ((discount/100) * productPrice);
    }

    public void deleteProduct(long productId)
    {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "productId", productId
                ));

        productRepo.delete(product);
    }


    private ProductDTO convertToDTO(Product savedProduct)
    {
        ProductDTO dto = new ProductDTO();

        dto.id = savedProduct.getId();
        dto.imgUrl = savedProduct.getProductImage();
        dto.productName = savedProduct.getProductName();
        dto.description = savedProduct.getDescription();
        dto.categoryId = savedProduct.getCategory().getId();
        dto.productPrice = savedProduct.getProductPrice();
        dto.discount = savedProduct.getDiscount();
        dto.finalPrice = savedProduct.getFinalPrice();
        dto.totalQuantity = savedProduct.getAvailableProductQuantity();
        dto.sellerId = savedProduct.getSellerId();

        return dto;
    }

    public ProductDTO getProductObjForProductId(long productId)
    {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new APIException("Product not found!"));

        return convertToDTO(product);
    }

    public PageDetails<ProductDTO> searchProducts(String name, String category, int pageNum, int pageSize, String sortOrder, String sortBy) {

        Sort sortOrderAndBy = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortOrderAndBy);

        // Normalize empty strings to null for query compatibility
        //name.trim() -> removes leading and trailing spaces
        name = (name != null && !name.trim().isEmpty()) ? name : null;
        category = (category != null && !category.trim().isEmpty()) ? category : null;

        Page<Product> productPage = productRepo.searchByNameAndCategoryName(name, category, pageDetails);

        if(productPage.isEmpty())
        {
            throw new APIException("No Products Available!");
        }

        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return new PageDetails<>(
                productDTOS,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public PageDetails<ProductDTO> fetchProductsBySeller(long sellerId, int pageNum, int pageSize, String sortOrder, String sortBy)
    {
        Sort sortOrderAndBy = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum, pageSize, sortOrderAndBy);
        Page<Product> productPage = productRepo.findAllBySellerId(sellerId, pageDetails);

        if(productPage.isEmpty())
        {
            throw new APIException("No products available for seller!");
        }

        List<ProductDTO> productDTOS = productPage.getContent().stream().map(this::convertToDTO).toList();

        return new PageDetails<>(
                productDTOS,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public long sellerIdForProduct(long productId)
    {
        return productRepo.getReferenceById(productId).getSellerId();
    }
}
