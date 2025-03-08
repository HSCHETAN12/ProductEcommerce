package com.Virima.ProductEcommerce.ServiceImplemantation;


import com.Virima.ProductEcommerce.Entity.Category;
import com.Virima.ProductEcommerce.Entity.Products;
import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Helper.CloudinaryHelper;
import com.Virima.ProductEcommerce.Repo.CategoryRepo;
import com.Virima.ProductEcommerce.Repo.ProductRepo;
import com.Virima.ProductEcommerce.Repo.UsersRepo;
import com.Virima.ProductEcommerce.Security.JWTService;
import com.Virima.ProductEcommerce.dto.CategoryDto;
import com.Virima.ProductEcommerce.dto.ProductDto;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.Virima.ProductEcommerce.Entity.ProductStatus.ACTIVE;
import static com.Virima.ProductEcommerce.Entity.ProductStatus.INACTIVE;

@Service
public class AdminServiceImp {

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CloudinaryHelper cloudinaryHelper;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    JWTService jwtService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public ResponseEntity<Object> verify(UserloginDto user) {
        Users admin1 = usersRepo.findByusername(user.getUsername());
        Map<String, Object> map = new HashMap<>();
        if (admin1 != null && bCryptPasswordEncoder.matches(user.getPassword(), admin1.getPassword())) {
            // Generate JWT token for Admin
            String token = jwtService.generateToken(admin1.getUsername(),admin1.getRole().getName());


            map.put("message", "Login Successful");
            map.put("token", token);  // Return JWT token
            return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
        } else {

            map.put("message", "Invalid login credentials");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Object> fetchUsers(int id) {
        Optional<Users> users=usersRepo.findById(id);
        Map<String, Object> map = new HashMap<>();
        if(users==null)
        {
            map.put("message","No user present in the database");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
        else{
            map.put("data",users);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }



    public ResponseEntity<Object> addCategory(CategoryDto categoryDto) {
        Category category=new Category();
        category.setName(categoryDto.getName());
        categoryRepo.save(category);
        Map<String, Object> map = new HashMap<>();
        map.put("message","Catregory added successfully");
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    public ResponseEntity<Object> product(String name, double price, int stock, String category, MultipartFile image) {
        Category category1=categoryRepo.findByName(category);
        Map<String, Object> map = new HashMap<>();
        if (category1==null) {
            map.put("message","No category found and add catogery first");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else{
        Products product=new Products();
        product.setName(name);
        product.setCategory(category1);
        product.setPrice(price);
        product.setStock(stock);
        if(stock>=1)
        {
            product.setStatus(ACTIVE);
        }else{
            product.setStatus(INACTIVE);
        }
        product.setCreatedBy("ADMIN");
        product.setUpdatedBy("ADMIN");
        product.setImageUrl(cloudinaryHelper.saveImage(image));
        productRepo.save(product);
        map.put("message","Product added Successfully");
        return new ResponseEntity<>(map,HttpStatus.ACCEPTED);
    }
    }

    public ResponseEntity<Object> fetchProducts() {
        Map<String, Object> map = new HashMap<>();
        List<Products> productList=productRepo.findByIsDeletedFalseAndStockGreaterThan(1);
        if(productList.isEmpty())
        {
            map.put("message","No product present in the shope");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else {
            map.put("data",productList);
            map.put("message","Product List");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchByCategory(String category) {
        Map<String, Object> map = new HashMap<>();
        List<Products> productList = productRepo.findByCategory(category);
        if (productList.isEmpty()) {
            map.put("message", "category not found");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("data", productList);
            map.put("message", "the Product List based on the category");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchByName(String name) {

    }
}

