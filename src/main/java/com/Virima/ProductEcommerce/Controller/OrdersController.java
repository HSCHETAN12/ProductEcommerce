package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdersController {

    @Autowired
    OrdersService ordersService;


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/checkouts")
    public ResponseEntity<Object> checkouts(HttpServletRequest request) throws ProductException {

        return ordersService.checkouts(request);
    }
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/cancleorders/{orderId}")
    public ResponseEntity<Object> cancleorders(@PathVariable int orderId, HttpServletRequest request)  {
        return ordersService.cancleorders(orderId,request);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<Object> fetchOrders(HttpServletRequest request,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return ordersService.fetchOrders(request,status,page,size);
    }
}
