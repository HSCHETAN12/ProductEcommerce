package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Entity.Wallet;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.UsersRepo;
import com.Virima.ProductEcommerce.Repo.WalletRepo;
import com.Virima.ProductEcommerce.Security.JWTService;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UsersServiceImp {

    @Autowired
    UsersRepo userRepository;

    @Autowired
    HelperMethods helperMethods;

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    JWTService jwtService;

    @Autowired
    WalletRepo walletRepo;


    Map<String, Object> map = new HashMap<>();

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public ResponseEntity<Object> save(@Valid UsersSignupDto user, BindingResult bindingResult) {
        // Check for existing email, mobile, and username
        helperMethods.validateUserFields(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return helperMethods.buildErrorResponse(bindingResult);
        }

        // No validation errors, proceed with saving user
        helperMethods.saveUser(user);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "User added successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Object> userLogin(UserloginDto user) {
        Users users = usersRepo.findByusername(user.getUsername());

        if (users != null && bCryptPasswordEncoder.matches(user.getPassword(), users.getPassword())) {
            // Generate JWT token for Admin
            String token = jwtService.generateToken(users.getUsername(),users.getRole().getName());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);  // Return JWT token
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid login credentials");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Object> fetch(int id) {
        Optional<Users> users = usersRepo.findById(id);
        Map<String, Object> map1 = new HashMap<>();
        if (users == null) {
            map1.put("message", "No user present in the database");
            return new ResponseEntity<>(map1, HttpStatus.NOT_FOUND);
        } else {
            map1.put("data", users);
            return new ResponseEntity<>(map1, HttpStatus.OK);
        }
    }
    /**
     * This method verifies the OTP provided by the user and updates their verification status.
     * It checks if the user exists by ID, compares the provided OTP with the stored one, and if valid,
     * marks the user as verified, creates a new wallet for the user, and saves the updates to the database.
     * If the OTP is invalid or the user is not found, it returns an appropriate error message.
     */
    public ResponseEntity<Object> verify(int otp, int id) {
        Map<String, Object> map = new HashMap<>();

        Optional<Users> user = usersRepo.findById(id);
        if (user != null) {
            if (otp == user.get().getOtp()) {
                user.get().setVerified(true);
                user.get().setOtp(0);
                Wallet wallet = new Wallet();
                wallet.setBalance((long) 0.0); // Initial balance
                wallet.setUser(user.get()); // Set the user for wallet
                user.get().setWallet(wallet);
                walletRepo.save(wallet);
                usersRepo.save(user.get());
                map.put("message", "user get verified");
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                map.put("message", "Invalied OTP");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
        } else {
            map.put("message", "User Not Found with given id");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }
}

