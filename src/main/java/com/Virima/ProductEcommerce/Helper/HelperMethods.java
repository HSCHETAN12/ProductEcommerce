package com.Virima.ProductEcommerce.Helper;

import com.Virima.ProductEcommerce.Entity.Cart;
import com.Virima.ProductEcommerce.Entity.Products;
import com.Virima.ProductEcommerce.Entity.Role;
import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Repo.ProductRepo;
import com.Virima.ProductEcommerce.Repo.RoleRepo;
import com.Virima.ProductEcommerce.Repo.UsersRepo;
import com.Virima.ProductEcommerce.Security.JWTService;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class HelperMethods {
    @Autowired
    UsersRepo userRepository;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    EmailSender emailSender;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    private JWTService jwtService;

    Map<String, Object> map = new HashMap<>();

    public void validateUserFields(UsersSignupDto user, BindingResult bindingResult) {
        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email is already exist");
        }
        if (userRepository.existsByMobile(user.getMobile())) {
            bindingResult.rejectValue("mobile", "error.mobile", "Number is already exist");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username is already taken");
        }
    }

    public ResponseEntity<Object> buildErrorResponse(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        bindingResult.getAllErrors().forEach(error -> {
            errors.append(error.getDefaultMessage()).append("\n");
        });

        Map<String, Object> map = new HashMap<>();
        map.put("message", errors.toString());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public Users saveUser(UsersSignupDto user) {
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        Optional<Role> role = roleRepo.findById(2);
        // Generate OTP
        int otp = generateOtp();
        System.err.print(otp);

        // Create Users entity and save it
        Users users = new Users();
        users.setFirstname(user.getFirstname());
        users.setLastname(user.getLastname());
        users.setUsername(user.getUsername());
        users.setPassword(user.getPassword());
        users.setEmail(user.getEmail());
        users.setOtp(otp);
        users.setGender(user.getGender());
        users.setConfirmpassword(user.getConfirmpassword());
        users.setMobile(user.getMobile());
        users.setRole(role.get());

        // Send OTP email
        emailSender.sendOtp(user.getEmail(), otp, user.getUsername());

        // Save user to repository
        userRepository.save(users);
        return users;
    }

    private String hashPassword(String password) {
        return new BCryptPasswordEncoder(12).encode(password);
    }

    private int generateOtp() {
        return ThreadLocalRandom.current().nextInt(10000, 100000);  // Using ThreadLocalRandom for better performance in multi-threaded environments
    }

    public void updateCartTotalAmount(Cart cart) {
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        // Sum up the total amount of the cart by fetching the product by its ID
        double totalAmount = cart.getCartItems().stream()
                .mapToDouble(item -> {
                    // Fetch the product using the productId from the CartItem
                    Products product = null;
                    try {
                        product = productRepo.findById(item.getProductId())
                                .orElseThrow(() -> new ProductException("Product not found"));
                    } catch (ProductException e) {
                        throw new RuntimeException(e);
                    }

                    // Calculate total price for this item and add to the sum
                    return item.getQuantity() * product.getPrice();
                })
                .sum();

        cart.updateTotalAmount(totalAmount);  // Update total amount using the method in Cart entity
    }

    public Users role(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
//            username = jwtService.extractUserName(token); // Extract username from the token
            System.err.println(token);
            username = jwtService.extractUsername(token);
            System.out.println(username);
        }
        Users user = userRepository.findByusername(username);
        System.out.println(user.getUsername());
        return user;
//        String role = jwtService.extractRole(token);
//        System.out.println(role);
//        return role;
    }

}
