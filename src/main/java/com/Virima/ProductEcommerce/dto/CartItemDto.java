package com.Virima.ProductEcommerce.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private int id;
    private int productId;
    private int quantity;
}
