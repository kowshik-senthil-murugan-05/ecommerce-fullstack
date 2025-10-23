package com.project.ecommerce.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepo extends JpaRepository<Category, Long>
{
    @Query("SELECT c FROM Category c WHERE LOWER(c.categoryName) = LOWER(:categoryName)")
    Category findByCategoryNameIgnoreCase(@Param("categoryName") String categoryName);
}
