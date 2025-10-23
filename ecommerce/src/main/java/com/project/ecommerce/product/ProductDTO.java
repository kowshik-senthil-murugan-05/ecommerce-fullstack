package com.project.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductDTO
{
    public long id;
    @NotBlank
    @Size(min = 3, message = "Size must be atleast of 3 characters")
    public String productName;
    public String description;
    public long categoryId;
    public double productPrice;
    public String imgUrl;
    public double discount;
    public double finalPrice;
    public int totalQuantity;
    public long sellerId;

}
