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
