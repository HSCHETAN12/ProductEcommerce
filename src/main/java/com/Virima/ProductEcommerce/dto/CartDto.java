package com.Virima.ProductEcommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private int id;
    private int userId;
    private double totalAmount;
    private String status;
    private List<CartItemDto> cartItems;
}
