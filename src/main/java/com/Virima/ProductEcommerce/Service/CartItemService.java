package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.Exception.ProductException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface CartItemService {
    ResponseEntity<Object> softDeleteProductFromCart(HttpServletRequest request, int cartItemId) throws ProductException;
}
