package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.Exception.ProductException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface OrdersService {
    ResponseEntity<Object> fetchOrders(HttpServletRequest request, String status, int page, int size);

    ResponseEntity<Object> checkouts(HttpServletRequest request) throws ProductException;

    ResponseEntity<Object> cancleorders(int orderId, HttpServletRequest request);
}
