package com.Virima.ProductEcommerce.Helper;

import com.Virima.ProductEcommerce.Entity.Role;
import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Repo.RoleRepo;
import com.Virima.ProductEcommerce.Repo.UsersRepo;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

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

    public void saveUser(UsersSignupDto user) {
        // Hash the password
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        Optional<Role> role=roleRepo.findById(2);
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
    }

    private String hashPassword(String password) {
        return new BCryptPasswordEncoder(12).encode(password);
    }

    private int generateOtp() {
        return ThreadLocalRandom.current().nextInt(10000, 100000);  // Using ThreadLocalRandom for better performance in multi-threaded environments
    }
}
