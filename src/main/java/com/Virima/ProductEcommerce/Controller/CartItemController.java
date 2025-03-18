package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartItemController {

    @Autowired
    CartItemService cartItemService;

    @PutMapping("/cart/soft-delete/{cartItemId}")
    public ResponseEntity<Object> softDeleteProductFromCart(HttpServletRequest request, @PathVariable int cartItemId) throws ProductException {
        return cartItemService.softDeleteProductFromCart(request,cartItemId);
    }
}
