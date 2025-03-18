package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.*;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Helper.EmailSender;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.*;
import com.Virima.ProductEcommerce.Security.JWTService;
import com.Virima.ProductEcommerce.Service.UsersService;
import com.Virima.ProductEcommerce.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;

import static com.Virima.ProductEcommerce.Entity.ProductStatus.AVAILABLE;
import static com.Virima.ProductEcommerce.Entity.ProductStatus.NOT_AVAILABLE;

@Service
public class UsersServiceImp implements UsersService {

    @Autowired
    UsersRepo userRepository;

    @Autowired
    HelperMethods helperMethods;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    JWTService jwtService;

    @Autowired
    OrderedItemsRepo orderedItemsRepo;

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    OrderAudictRepository orderAudictRepository;

    @Autowired
    PromocodeRepo promocodeRepo;

    @Autowired
    WalletAuditRepo walletAuditRepo;

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    EmailSender emailSender;

    @Autowired
    com.Virima.ProductEcommerce.Helper.checkOutMethods checkOutMethods;

    @Autowired
    TransactionRepository transactionRepository;


    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    /**
     * Registers a new user by validating the input and saving the user to the database.
     *
     * 1. Validates the provided user fields (email, mobile, username) for any existing entries.
     * 2. If validation errors exist, returns a response with the error details.
     * 3. If no errors, proceeds with saving the new user.
     * 4. Returns a 200 (OK) response with a success message after the user is saved.
     *
     * @param user The user signup details.
     * @param bindingResult Contains any validation errors for the user fields.
     * @return A ResponseEntity with a success message or validation error details and an appropriate HTTP status code.
     */

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


    /**
     * Handles user login requests.
     *
     * This method authenticates the user by verifying the provided username and password.
     * If the credentials are valid, a JWT token is generated for the user, and their
     * information is stored in the session. If the login is successful, the JWT token is
     * returned in the response. Otherwise, an error message is returned indicating invalid
     * credentials.
     *
     * @param user The UserloginDto object that contains the login credentials (username and password).
     * @param session The HttpSession object to store the logged-in user's information.
     * @return A ResponseEntity containing a message and either the JWT token if login is successful,
     *         or an error message if the credentials are invalid.
     * @throws NullPointerException if the user is not found or any null values are encountered during login.
     */
    public ResponseEntity<Object> userLogin(UserloginDto user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Users users = usersRepo.findByusername(user.getUsername());

        if (users != null && bCryptPasswordEncoder.matches(user.getPassword(), users.getPassword())) {
            // Generate JWT token for Admin
//            String token = jwtService.generateToken(users.getUsername(), users.getRole().getName());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Login Successful");
//            response.put("token", token);  // Return JWT token
//            session.setAttribute("user", users);
//            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

                    if (users.isVerified()) {
                        String token = jwtService.generateToken(users.getUsername(), users.getRole().getName());


                        response.put("message", "Login Successful");
                        response.put("token", token);  // Return JWT token
                        return new ResponseEntity<>(response,HttpStatus.OK);
                    } else {
                        int otp = new Random().nextInt(10000, 100000);
                        users.setOtp(otp);
                        System.err.println(otp);
                         emailSender.sendOtp(users.getEmail(), otp, users.getFirstname());
                        userRepository.save(users);
                       response.put("message","Otp sent to email first verifi to login");
                       return new ResponseEntity<>(response,HttpStatus.OK);
                    }
        } else {
            response.put("message", "Invalid login credentials");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

    /**
     * Adds products to a user's active cart, validating stock availability and updating the cart's total amount.
     *
     * 1. Retrieves or creates an active cart for the user.
     * 2. For each product in the cart request:
     *    - Validates if the product exists, is available, and has enough stock.
     *    - If the product is not already in the cart, creates a new cart item and adds it.
     *    - If the product is already in the cart, updates the quantity.
     * 3. Updates the stock of the product based on the cart's quantity and marks it as unavailable if stock runs out.
     * 4. Updates the cart’s total amount and saves the cart.
     * 5. Returns a 200 (OK) response with a success message if the products are successfully added to the cart.
     *    - If any error occurs (e.g., product not found, insufficient stock), returns a corresponding error message.
     *
     * @param request The HTTP request containing user info.
     * @param cart A list of CartRequest objects containing product IDs and quantities.
     * @return A ResponseEntity with a success or error message and an appropriate HTTP status code.
     * @throws ProductException If there is an issue with product retrieval or validation.
     */


    /**
     * Fetches all orders placed by the logged-in user.
     *
     * This method performs the following:
     * 1. Retrieves the logged-in user using the helper method.
     * 2. Queries the order repository to fetch all orders associated with the user's ID.
     * 3. If no orders are found, it returns a message stating "No orders" with an HTTP 200 OK response.
     * 4. If orders are found, it converts each order into an `OrdersDto` object, including details like order ID, user ID, total price, order status, cart ID, and promo code ID.
     * 5. The list of `OrdersDto` objects is returned in the response with an HTTP 200 OK status.
     *
     * @param request The HTTP request containing user authentication details.
     * @return A `ResponseEntity` containing the list of orders (if available) in the form of `OrdersDto` and the corresponding HTTP status.
     */
    public ResponseEntity<Object> fetchuserorders(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethods.role(request);


        List<Orders> orders = orderRepository.findByUserId(user.getId());

        if (orders.isEmpty()) {
            map.put("message", "No orders");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            List<OrdersDto> ordersDtoList = new ArrayList<>();

            // Convert each order to an OrdersDto
            for (Orders order : orders) {
                OrdersDto ordersDto = new OrdersDto();
                ordersDto.setId(order.getId());
                ordersDto.setUserId(order.getUserId());
                ordersDto.setTotalPrice(order.getTotalPrice());
                ordersDto.setOrderStatus(order.getOrderStatus());

                ordersDto.setCartId(order.getCart().getId());
                ordersDto.setPromoCodeId(order.getPromoCode().getId());

                // Add the OrdersDto to the list
                ordersDtoList.add(ordersDto);
            }

            // Add the list of OrdersDto to the response map
            map.put("data", ordersDtoList);
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
}


