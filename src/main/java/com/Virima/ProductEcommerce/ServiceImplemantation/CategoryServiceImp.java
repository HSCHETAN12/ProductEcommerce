package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Category;
import com.Virima.ProductEcommerce.Entity.Products;
import com.Virima.ProductEcommerce.Repo.CategoryRepo;
import com.Virima.ProductEcommerce.Repo.ProductRepo;
import com.Virima.ProductEcommerce.Service.CategoryService;
import com.Virima.ProductEcommerce.dto.CategoryDto;
import com.Virima.ProductEcommerce.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    ProductRepo productRepo;

    /**
     * Adds a new category to the database.
     * <p>
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

        // Check if category exists
        Category existingCategory = categoryRepo.findByName(categoryDto.getName());
        if (existingCategory != null) {
            map.put("message", "Category already exists in the database");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        // Create new category
        Category category = new Category();
        category.setName(categoryDto.getName());
        categoryRepo.save(category);

        map.put("message", "Category added successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * Updates an existing category in the database.
     * <p>
     * 1. Fetches the category by its ID. If not found, returns a 404 (NOT_FOUND) response.
     * 2. If the category exists and the name is provided in the DTO, updates the category's name.
     * 3. Saves the updated category to the database.
     * 4. Returns a 200 (OK) response with a success message if the update is successful.
     *
     * @param id          The ID of the category to be updated.
     * @param categoryDto The data transfer object containing the updated category details (e.g., name).
     * @return A ResponseEntity with a message and HTTP status code.
     */

    public ResponseEntity<Object> UpdateCategory(int id, CategoryDto categoryDto) {
        Map<String, Object> map = new HashMap<>();

        // Fetch the category by ID
        Category category = categoryRepo.findById(id).orElse(null);

        if (category != null) {

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

    public ResponseEntity<Object> fetchCategory() {
        try {
            Map<String, Object> map = new HashMap<>();
            List<Category> list = categoryRepo.findAll();
            if (list.isEmpty()) {
                map.put("message", "No category found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            } else {
                map.put("data", list);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> getProductsByCategory(String categoryName) {
        Map<String, Object> response = new HashMap<>();

        // Find the category by name
        Category categoryOpt = categoryRepo.findByName(categoryName);
        if (categoryOpt == null) {
            response.put("message", "Category Not Found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Category category = categoryOpt;

        // Fetch products for the found category
        List<Products> products = productRepo.findByCategoryAndIsDeletedFalse(category);
        if (products.isEmpty()) {
            response.put("message", "No products found for this category");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Convert Products to ProductDto
        List<ProductDto> productDtoList = products.stream().map(product -> {
            ProductDto dto = new ProductDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            dto.setStatus(product.getStatus().toString());
            dto.setImageUrl(product.getImageUrl());
            return dto;
        }).collect(Collectors.toList());

        response.put("data", productDtoList);
        response.put("message", "Product List");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


