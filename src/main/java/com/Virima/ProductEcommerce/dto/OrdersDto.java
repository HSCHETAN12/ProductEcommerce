package com.Virima.ProductEcommerce.dto;

import com.Virima.ProductEcommerce.Entity.Cart;
import com.Virima.ProductEcommerce.Entity.PromoCode;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class OrdersDto {

    private int id;
    private int userId;
    private double totalPrice;
    private String orderStatus;

    private int cartId;


    private Integer  promoCodeId;
}
