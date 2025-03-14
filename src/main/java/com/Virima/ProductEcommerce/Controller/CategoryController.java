package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Service.CategoryService;
import com.Virima.ProductEcommerce.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/category")
    public ResponseEntity<Object> addCategory(@RequestBody CategoryDto categoryDto)
    {
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/category/{id}")
    public ResponseEntity<Object> UpdateCategory(@PathVariable int id, @RequestBody CategoryDto categoryDto)
    {
        return categoryService.UpdateCategory(id,categoryDto);
    }

    
}
