package com.Virima.ProductEcommerce.dto;

import com.Virima.ProductEcommerce.Entity.Category;
import lombok.Data;

@Data
public class ProductDto {
    private String name;
    private Category category;
    private double price;

    private int stock;

    private String imageUrl;

}
