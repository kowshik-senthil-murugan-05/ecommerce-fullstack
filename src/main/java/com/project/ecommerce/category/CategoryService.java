package com.project.ecommerce.category;

import com.project.ecommerce.util.PageDetails;

import java.util.Optional;

public interface CategoryService
{
    CategoryDTO addCategory(CategoryDTO dto);

    PageDetails<CategoryDTO> listAllCategories(int pageNum, int pageSize, String sortBy, String sortOrder);

    void removeCategory(long categoryId);

    CategoryDTO updateCategory(CategoryDTO dto);

    boolean isValidCategory(long categoryId);

    Optional<Category> getCategoryObj(long categoryId);
}
