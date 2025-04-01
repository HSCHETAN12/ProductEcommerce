package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Service.CartService;
import com.Virima.ProductEcommerce.dto.CartRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
public class CartController {

    @Autowired
    CartService cartService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/cart/add")
    public ResponseEntity<Object> addCart(@RequestBody List<CartRequest> cart, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            return cartService.addProductsToCart(request, cart);
        } catch (ProductException e) {
            map.put("message", "Product Not Found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/cart/remove")
    public ResponseEntity<Object> DeleteCartItems(@RequestParam int productId,HttpServletRequest request) throws ProductException {
        return cartService.DeleteCartItems(productId,request);
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/cart/add")
    public ResponseEntity<Object> fetchCart(HttpServletRequest request) throws ProductException {
        return cartService.fetchCart(request);
    }
}
