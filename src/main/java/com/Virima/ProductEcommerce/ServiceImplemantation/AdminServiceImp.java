package com.Virima.ProductEcommerce.ServiceImplemantation;


import com.Virima.ProductEcommerce.Entity.*;
import com.Virima.ProductEcommerce.Helper.CloudinaryHelper;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.*;
import com.Virima.ProductEcommerce.Security.JWTService;
import com.Virima.ProductEcommerce.Service.AdminService;
import com.Virima.ProductEcommerce.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.HttpBasicDsl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.Virima.ProductEcommerce.Entity.ProductStatus.AVAILABLE;
import static com.Virima.ProductEcommerce.Entity.ProductStatus.NOT_AVAILABLE;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    WalletAuditRepo walletAuditRepo;

    @Autowired
    CloudinaryHelper cloudinaryHelper;

    @Autowired
    PromocodeRepo promocodeRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    JWTService jwtService;

    @Autowired
    HelperMethods methods;

    @Autowired
    OrderRepository orderRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

//    public ResponseEntity<Object> verify(UserloginDto user) {
//        Users admin1 = usersRepo.findByusername(user.getUsername());
//        Map<String, Object> map = new HashMap<>();
//        if (admin1 != null && bCryptPasswordEncoder.matches(user.getPassword(), admin1.getPassword())) {
//            // Generate JWT token for Admin
//            String token = jwtService.generateToken(admin1.getUsername(), admin1.getRole().getName());
//
//
//            map.put("message", "Login Successful");
//            map.put("token", token);  // Return JWT token
//            return new ResponseEntity<>(map, HttpStatus.ACCEPTED);
//        } else {
//
//            map.put("message", "Invalid login credentials");
//            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//        }
//    }



    public ResponseEntity<Object> fetchUsers(int id) {

        Optional<Users> user = usersRepo.findById(id);
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            map.put("message", "No user present in the database");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            UsersSignupDto userDTO = new UsersSignupDto(
                    user.get().getId(),
                    user.get().getFirstname(),
                    user.get().getLastname(),
                    user.get().getUsername(),
                    user.get().getEmail(),
                    user.get().getPassword(),
                    user.get().getWallet().getBalance(),
                    user.get().getMobile(),
                    user.get().getGender());
            map.put("data", userDTO);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> addCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        categoryRepo.save(category);
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Catregory added successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

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
        List<Products> productList = productRepo.findByIsDeletedFalseAndStockGreaterThan(1);
        if (productList.isEmpty()) {
            map.put("message", "No product present or out of stock");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("data", productList);
            map.put("message", "Product List");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchByCategory(String category) {
        Map<String, Object> map = new HashMap<>();
        List<Products> productList = productRepo.findByCategory_NameAndIsDeletedFalseAndStockGreaterThan(category, 1);
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

    public ResponseEntity<Object> fetchUser() {
        Map<String, Object> map = new HashMap<>();
        List<Users> users = usersRepo.findVerifiedAndNotDeleted();

        if (users.isEmpty()) {
            map.put("message", "No verified users exist in the database");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {

            List<UsersSignupDto> userDetails = new ArrayList<>();

            for (Users user : users) {
                // Creating a UserDTO for each user
                UsersSignupDto userDTO = new UsersSignupDto(
                        user.getId(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getWallet().getBalance(),
                        user.getMobile(),
                        user.getGender()
                );

                userDetails.add(userDTO);
            }

            map.put("data", userDetails); // Adding the user details to the response
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> WalletTopUp(WalletTopUpDto walletTopUpRequest) {
        Map<String, Object> map = new HashMap<>();
        Wallet wallet = walletRepo.findByUserId(walletTopUpRequest.getUserId());
        if (wallet == null) {
            map.put("message", "User's wallet not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        double newBalance = wallet.getBalance() + walletTopUpRequest.getAmount();
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);

        WalletAudit walletAudit = new WalletAudit();
        walletAudit.setUserId(walletTopUpRequest.getUserId());
        walletAudit.setTransactionType("CREDIT");
        walletAudit.setAmount(walletTopUpRequest.getAmount());
        walletAudit.setBalanceAfterTransaction(newBalance);
        walletAuditRepo.save(walletAudit);
        map.put("message", "Wallet topup done to given user id");
        map.put("data", walletTopUpRequest);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Object> createPromoCode(PromoCodeDto promoCodeDto) {
        Map<String, Object> map = new HashMap<>();
        Optional<PromoCode> promoCode = promocodeRepo.findByCodeAndStatus(promoCodeDto.getCode(), PromoCodeStatus.ACTIVE);
        System.out.println(promoCode);
        if (!promoCode.isEmpty()) {
            map.put("message", "Promocode Exists in the database");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            PromoCode promoCode1 = new PromoCode(promoCodeDto.getCode(), promoCodeDto.getDiscountValue(), promoCodeDto.getType(), promoCodeDto.getStartDate(), promoCodeDto.getEndDate(), promoCodeDto.getStatus(), promoCodeDto.getProductName());
            if(promoCodeDto.getType().equals(PromoCodeType.ORDER_BASED))
            {
                promoCode1.setProductName(null);
            }
            promocodeRepo.save(promoCode1);
            map.put("message", "Promocode Added successfully");
            map.put("data", promoCode);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchWalletAudict(int id, HttpServletRequest request) {
        List<WalletAudit> usersAudict=walletAuditRepo.findByUserId(id);
        Map<String, Object> map = new HashMap<>();
        if(usersAudict.isEmpty())
        {
            map.put("message","No user wallet tranaction found");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else {
            map.put("message","user wallet tranaction are");
            map.put("data",usersAudict);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }

//    public ResponseEntity<Object> fetchOrders(HttpServletRequest request) {
//        Map<String, Object> map = new HashMap<>();
//        List<Orders> orders=orderRepository.findByOrderStatus("completed");
//        if(orders.isEmpty())
//        {
//         map.put("message","No orders are completed");
//         return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//        }
//        map.put("data",orders);
//        return new ResponseEntity<>(map,HttpStatus.OK);
//    }
}

