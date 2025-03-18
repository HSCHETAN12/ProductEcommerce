package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Category;
import com.Virima.ProductEcommerce.Entity.Products;
import com.Virima.ProductEcommerce.Helper.CloudinaryHelper;
import com.Virima.ProductEcommerce.Repo.CategoryRepo;
import com.Virima.ProductEcommerce.Repo.ProductRepo;
import com.Virima.ProductEcommerce.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.Virima.ProductEcommerce.Entity.ProductStatus.AVAILABLE;
import static com.Virima.ProductEcommerce.Entity.ProductStatus.NOT_AVAILABLE;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    CloudinaryHelper cloudinaryHelper;

    @Autowired
    ProductRepo productRepo;

    /**
     * Adds a new product to the database.
     *
     * 1. Checks if the specified category exists. If not, returns a message asking to add the category first.
     * 2. Creates a new product and sets its properties (name, price, stock, category, and image).
     * 3. Sets the product status to "AVAILABLE" if the stock is greater than or equal to 1, otherwise sets it to "NOT_AVAILABLE".
     * 4. Sets the "createdBy" and "updatedBy" fields to "ADMIN".
     * 5. Saves the product to the database.
     * 6. Returns a 202 (ACCEPTED) response with a success message upon successful addition.
     *
     * @param name The name of the product.
     * @param price The price of the product.
     * @param stock The stock quantity of the product.
     * @param category The category of the product.
     * @param image The image file for the product.
     * @return A ResponseEntity with a message and HTTP status code.
     */

    public ResponseEntity<Object> product(String name, double price, int stock, String category, MultipartFile image) {
        Category category1 = categoryRepo.findByName(category);
        Map<String, Object> map = new HashMap<>();
        if (category1 == null) {
            map.put("message", "No category found and add catogery first");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            Products product = new Products();
            product.setName(name);
            product.setCategory(category1);
            product.setPrice(price);
            product.setStock(stock);
            if (stock >= 1) {
                product.setStatus(AVAILABLE);
            } else {
                product.setStatus(NOT_AVAILABLE);
            }
            product.setCreatedBy("ADMIN");
            product.setUpdatedBy("ADMIN");
            product.setImageUrl(cloudinaryHelper.saveImage(image));
            productRepo.save(product);
            map.put("message", "Product added Successfully");
            return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
        }
    }

    /**
     * Fetches a list of available products that are not marked as deleted and have stock greater than zero.
     *
     * 1. Retrieves all products that are not deleted and have a positive stock.
     * 2. If no products are found, returns a 200 (OK) response with a message indicating no products or out of stock.
     * 3. If products are found, returns a 200 (OK) response with the list of products and a success message.
     *
     * @return  A ResponseEntity containing the list of products (if available) and appropriate message.
     */

    public ResponseEntity<Object> fetchProducts() {
        Map<String, Object> map = new HashMap<>();
        List<Products> productList = productRepo.findByIsDeletedFalseAndStockGreaterThan(0);
        if (productList.isEmpty()) {
            map.put("message", "No product present or out of stock");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("data", productList);
            map.put("message", "Product List");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }
    /**
     * Fetches a product by its name, ensuring it is not deleted and has stock greater than zero.
     *
     * 1. Searches for a product by its name that is not deleted and has stock greater than 1.
     * 2. If the product is not found, returns a 200 (OK) response with a message indicating the product was not found.
     * 3. If the product is found, returns a 200 (OK) response with the product details and a success message.
     *
     * @param name The name of the product to be fetched.
     * @return A ResponseEntity containing the product data (if found) and appropriate message.
     */

    public ResponseEntity<Object> fetchByName(String name) {
        Map<String, Object> map = new HashMap<>();
        Products product = productRepo.findByNameAndIsDeletedFalseAndStockGreaterThan(name, 1);
        if (product == null) {
            map.put("message", "Product Not Found by the name");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("message", "Product with the name");
            map.put("data", product);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /**
     * Fetches a product by its ID, ensuring it is not deleted.
     *
     * 1. Retrieves a product by its ID that is not marked as deleted.
     * 2. If the product is not found, returns a 200 (OK) response with a message indicating the product was not found.
     * 3. If the product is found, returns a 200 (OK) response with the product details.
     *
     * @param id The ID of the product to be fetched.
     * @return A ResponseEntity containing the product data (if found) and appropriate message.
     */

    public ResponseEntity<Object> fetchById(int id) {
        Map<String, Object> map = new HashMap<>();
        Optional<Products> product = productRepo.findByIdAndIsDeletedFalse(id);
        if (product.isEmpty()) {
            map.put("message", "Product Not Found");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("data", product);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }
    /**
     * Updates an existing product based on the provided details.
     *
     * 1. Fetches the product by its ID. If the product is not found, returns a 404 (NOT_FOUND) response.
     * 2. If a name is provided, updates the product's name.
     * 3. If a category is provided, updates the product's category, ensuring the category exists.
     *    If the category doesn't exist, returns a message asking to add the category first.
     * 4. If a price is provided, updates the product's price.
     * 5. If stock is provided, updates the stock quantity and sets the product status based on stock (AVAILABLE/NOT_AVAILABLE).
     * 6. If an image is provided, updates the product's image URL.
     * 7. Sets the "updatedBy" field to "ADMIN".
     * 8. Saves the updated product to the database.
     * 9. Returns a 200 (OK) response with a success message upon successful update.
     *
     * @param id The ID of the product to be updated.
     * @param name The new name of the product (optional).
     * @param price The new price of the product (optional).
     * @param stock The new stock quantity of the product (optional).
     * @param category The new category of the product (optional).
     * @param image The new image file for the product (optional).
     * @return A ResponseEntity with a message and HTTP status code.
     */

    public ResponseEntity<Object> productUpdate(int id, String name, Double  price, Integer stock, String category, MultipartFile image) {
        Products product = productRepo.findById(id).orElse(null);
        Map<String, Object> map = new HashMap<>();
        if (product == null) {
            map.put("message", "Product not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        if (name != null) {
            product.setName(name);
        }
        if (category != null) {
            Category category1 = categoryRepo.findByName(category);
            if (category1 != null) {
                product.setCategory(category1);
            } else {
                map.put("message", "Category not found, make sure to add category first.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }
        if (price !=null) {
            product.setPrice(price);
        }
        if (stock != null) {
            product.setStock(stock);
            if (stock >= 1) {
                product.setStatus(AVAILABLE);
            } else {
                product.setStatus(NOT_AVAILABLE);
            }
        }
        if (image != null) {
            product.setImageUrl(cloudinaryHelper.saveImage(image));
        }
        product.setUpdatedBy("ADMIN");
        productRepo.save(product);
        map.put("message", "Product updated successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    /**
     * Soft deletes a product by its ID.
     *
     * 1. Fetches the product by its ID. If the product is not found, returns a 404 (NOT_FOUND) response.
     * 2. If the product is found, marks it as deleted by setting the "deleted" flag to true.
     * 3. Returns a 200 (OK) response with a success message indicating the product was deleted.
     *
     * @param id The ID of the product to be soft deleted.
     * @return A ResponseEntity with a message and HTTP status code.
     */

    public ResponseEntity<Object> productDelete(int id) {
        Products product = productRepo.findById(id).orElse(null);
        Map<String, Object> map = new HashMap<>();
        if (product == null) {
            map.put("message", "Product not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }else {
            product.setDeleted(true);
            map.put("message","Product deleted");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

}
