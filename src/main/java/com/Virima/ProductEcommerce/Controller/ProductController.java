package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/products")
    public ResponseEntity<Object> saveProduct(@RequestParam("name") String name,
                                              @RequestParam("price") double price,
                                              @RequestParam("stock") int stock,
                                              @RequestParam("category") String category, @RequestParam("image") MultipartFile image) {
        return productService.product(name, price, stock, category, image);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/products")
    public ResponseEntity<Object> fetchProducts()
    {
        return productService.fetchProducts();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/products/name/{name}")
    public ResponseEntity<Object> fetchByName(@PathVariable String name) {
        return productService.fetchByName(name);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> fetchByName(@PathVariable int id) {
        return productService.fetchById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/products/{id}")
    public ResponseEntity<Object> fetchByName(@PathVariable int id,@RequestParam("name") String name,
                                              @RequestParam("price") Double  price,
                                              @RequestParam("stock") Integer stock,
                                              @RequestParam("category") String category, @RequestParam("image") MultipartFile image) {
        return productService.productUpdate(id,name, price, stock, category, image);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("product/delete/{id}")
    public ResponseEntity<Object> productDelete(@PathVariable int id) {
        return productService.productDelete(id);
    }

}
