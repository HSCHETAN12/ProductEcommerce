package com.Virima.ProductEcommerce.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ResponseEntity<Object> product(String name, double price, int stock, String category, MultipartFile image);

    ResponseEntity<Object> fetchProducts();

    ResponseEntity<Object> fetchByName(String name);

    ResponseEntity<Object> fetchById(int id);

    ResponseEntity<Object> productUpdate(int id, String name, Double  price, Integer stock, String category, MultipartFile image);

    ResponseEntity<Object> productDelete(int id);
}
