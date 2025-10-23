package com.project.ecommerce.category;

import jakarta.persistence.*;

@Entity
@Table(name = "ecomm_category")
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String categoryName;

    public Category() {
    }

    public Category(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public Category(long categoryId, String categoryName)
    {
        this.id = categoryId;
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + id +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
