package com.project.ecommerce.product;

import com.project.ecommerce.util.PageDetails;

import java.util.Optional;

public interface ProductService
{
    ProductDTO saveProduct(ProductDTO dto);

    PageDetails<ProductDTO> getAllProducts(int pageNum, int pageSize, String sortBy, String sortOrder);

    PageDetails<ProductDTO> getAllProductsForCategoryId(int pageNum, int pageSize, String sortBy, String sortOrder, long categoryId);

    ProductDTO updateProduct(ProductDTO dto);

    void deleteProduct(long productId);

    ProductDTO getProductObjForProductId(long productId);

    PageDetails<ProductDTO> searchProducts(String name, String category, int pageNum, int pageSize, String sortOrder, String sortBy);

    PageDetails<ProductDTO> fetchProductsBySeller(long sellerId, int pageNum, int pageSize, String sortOrder, String sortBy);

    long sellerIdForProduct(long productId);
}
