package com.project.ecommerce.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO
{
    public long id;
    @NotBlank
    @Size(min = 4, message = "Category name field must contain atleast 4characters!")
    public String categoryName;

    public CategoryDTO()
    {}

    public CategoryDTO(long id, String categoryName)
    {
        this.id = id;
        this.categoryName = categoryName;
    }
}
