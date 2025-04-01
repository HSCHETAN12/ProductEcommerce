package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.dto.CategoryDto;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<Object> addCategory(CategoryDto categoryDto);

    ResponseEntity<Object> UpdateCategory(int id,CategoryDto categoryDto);

    ResponseEntity<Object> fetchCategory();

    ResponseEntity<Object> getProductsByCategory(String categoryName);
}
