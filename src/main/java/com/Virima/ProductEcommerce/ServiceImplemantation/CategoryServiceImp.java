package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Category;
import com.Virima.ProductEcommerce.Repo.CategoryRepo;
import com.Virima.ProductEcommerce.Service.CategoryService;
import com.Virima.ProductEcommerce.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    CategoryRepo categoryRepo;

    public ResponseEntity<Object> addCategory(CategoryDto categoryDto) {
        Map<String, Object> map = new HashMap<>();
        Category category = categoryRepo.findByName(categoryDto.getName());
        if (category != null) {
            map.put("message", "Category is present in the database");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } else {
            Category categorys = new Category();
            category.setName(categoryDto.getName());
            categoryRepo.save(categorys);
            map.put("message", "Catregory added successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> UpdateCategory(int id, CategoryDto categoryDto) {
        Map<String, Object> map = new HashMap<>();

        // Fetch the category by ID
        Category category = categoryRepo.findById(id).orElse(null);

        if (category != null)
        {

            if (categoryDto.getName() != null) {

                category.setName(categoryDto.getName());
                categoryRepo.save(category);
            }
            map.put("message", "Category updated successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("message", "Category not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }
}

