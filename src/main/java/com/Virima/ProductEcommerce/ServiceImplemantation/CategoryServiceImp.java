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

    /**
     * Adds a new category to the database.
     *
     * 1. Checks if a category with the same name already exists in the database.
     * 2. If the category exists, returns a 400 (BAD_REQUEST) response with a message.
     * 3. If the category doesn't exist, creates a new Category object and saves it.
     * 4. Returns a 200 (OK) response with a success message.
     *
     * @param categoryDto The data transfer object containing category details (e.g., name).
     * @return A ResponseEntity with a message and HTTP status code.
     */
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

    /**
     * Updates an existing category in the database.
     *
     * 1. Fetches the category by its ID. If not found, returns a 404 (NOT_FOUND) response.
     * 2. If the category exists and the name is provided in the DTO, updates the category's name.
     * 3. Saves the updated category to the database.
     * 4. Returns a 200 (OK) response with a success message if the update is successful.
     *
     * @param id The ID of the category to be updated.
     * @param categoryDto The data transfer object containing the updated category details (e.g., name).
     * @return A ResponseEntity with a message and HTTP status code.
     */

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

