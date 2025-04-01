package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.*;
import com.Virima.ProductEcommerce.Helper.EmailSender;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.*;
import com.Virima.ProductEcommerce.Security.JWTService;
import com.Virima.ProductEcommerce.Service.UsersService;
import com.Virima.ProductEcommerce.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;

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
       Users users= helperMethods.saveUser(user);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "User added successfully");
        map.put("data",users);
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
                        response.put("Role",users.getRole().getName());
                        response.put("user",users);

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
                wallet.setBalance((long) 0.0);
                wallet.setUser(user.get());
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


                if (order.getCart() != null) {
                    ordersDto.setCartId(order.getCart().getId());
                }


                if (order.getPromoCode() != null) {
                    ordersDto.setPromoCodeId(order.getPromoCode().getId());
                } else {
                    ordersDto.setPromoCodeId(null);
                }

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

    public ResponseEntity<Object> resendOtp(int id) {
        Map<String, Object> map = new HashMap<>();
        Users user = usersRepo.findById(id).get();
        int otp = new Random().nextInt(100000, 1000000);
        user.setOtp(otp);
        System.err.println(otp);
         emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
        usersRepo.save(user);
       map.put("message","Otp resend successfull");
       return new ResponseEntity<>(map,HttpStatus.OK);

    }

    public ResponseEntity<Object> updateProfile(UsersUpdateDto user, HttpServletRequest request) {
        Users users = helperMethods.role(request);

        if (users == null) {
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }


        if (user.getUsername() != null && !user.getUsername().equals(users.getUsername())) {
            boolean usernameExists = userRepository.existsByUsername(user.getUsername());
            if (usernameExists) {
                return new ResponseEntity<>(Map.of("message", "Username already exists"), HttpStatus.BAD_REQUEST);
            }
        }


        if (user.getEmail() != null && !user.getEmail().equals(users.getEmail())) {
            boolean emailExists = userRepository.existsByEmail(user.getEmail());
            if (emailExists) {
                return new ResponseEntity<>(Map.of("message", "Email already exists"), HttpStatus.BAD_REQUEST);
            }
        }


        if (user.getMobile() != 0 && user.getMobile() != users.getMobile()) {
            boolean mobileExists = userRepository.existsByMobile(user.getMobile());
            if (mobileExists) {
                return new ResponseEntity<>(Map.of("message", "Mobile number already exists"), HttpStatus.BAD_REQUEST);
            }
        }


        if (user.getFirstname() != null) users.setFirstname(user.getFirstname());
        if (user.getLastname() != null) users.setLastname(user.getLastname());
        if (user.getUsername() != null) users.setUsername(user.getUsername());
        if (user.getEmail() != null) users.setEmail(user.getEmail());
        if (user.getMobile() != 0) users.setMobile(user.getMobile());
        if (user.getGender() != null) users.setGender(user.getGender());

        userRepository.save(users);

        return new ResponseEntity<>(Map.of("message", "User updated successfully", "data", users), HttpStatus.OK);
    }

    public ResponseEntity<Object> fetchProfile(HttpServletRequest request) {
        Users user = helperMethods.role(request);

        if (user == null) {
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }

        // Create response data without exposing sensitive details like password
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("firstname", user.getFirstname());
        userData.put("lastname", user.getLastname());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("mobile", user.getMobile());
        userData.put("gender", user.getGender());
        userData.put("balance", user.getWallet().getBalance());

        return new ResponseEntity<>(Map.of("message", "User details fetched successfully", "data", userData), HttpStatus.OK);
    }

}


