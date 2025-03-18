package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.dto.CartRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {
    ResponseEntity<Object> addProductsToCart(HttpServletRequest request, List<CartRequest> cart) throws ProductException;

    ResponseEntity<Object> DeleteCartItems(int productId, HttpServletRequest request) throws ProductException;

    ResponseEntity<Object> fetchCart(HttpServletRequest request) throws ProductException;
}
