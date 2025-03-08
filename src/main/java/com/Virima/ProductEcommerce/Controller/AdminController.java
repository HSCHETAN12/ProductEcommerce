package com.Virima.ProductEcommerce.Controller;


import com.Virima.ProductEcommerce.Entity.Products;
import com.Virima.ProductEcommerce.ServiceImplemantation.AdminServiceImp;
import com.Virima.ProductEcommerce.dto.CategoryDto;
import com.Virima.ProductEcommerce.dto.ProductDto;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminServiceImp adminServiceImp;

    @PostMapping("/login")
    public ResponseEntity<Object> adminLogin(@RequestBody UserloginDto user) {
        return adminServiceImp.verify(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchUser(@PathVariable int id) {
        return adminServiceImp.fetchUsers(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/category")
    public ResponseEntity<Object> addCategory(@RequestBody CategoryDto categoryDto)
    {
        return adminServiceImp.addCategory(categoryDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/products")
    public ResponseEntity<Object> saveProduct(@RequestParam("name") String name,
                                              @RequestParam("price") double price,
                                              @RequestParam("stock") int stock,
                                              @RequestParam("category") String category, @RequestParam("image") MultipartFile image) {
        return adminServiceImp.product(name, price, stock, category, image);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/products")
    public ResponseEntity<Object> fetchProducts()
    {
        return adminServiceImp.fetchProducts();
    }

    @GetMapping("/products/{category}")
    public ResponseEntity<Object> fetchByCategory(@PathVariable String category) {
        return adminServiceImp.fetchByCategory(category);
    }

    @GetMapping("/products/name/{name}")
    public ResponseEntity<Object> fetchByName(@PathVariable String name) {
        return adminServiceImp.fetchByName(name);
    }
}
