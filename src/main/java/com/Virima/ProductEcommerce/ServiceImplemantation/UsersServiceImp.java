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

//    public ResponseEntity<Object> fetch(int id) {
//        Optional<Users> users = usersRepo.findById(id);
//        Map<String, Object> map1 = new HashMap<>();
//        if (users == null) {
//            map1.put("message", "No user present in the database");
//            return new ResponseEntity<>(map1, HttpStatus.NOT_FOUND);
//        } else {
//            map1.put("data", users);
//            return new ResponseEntity<>(map1, HttpStatus.OK);
//        }
//    }

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


    public ResponseEntity<Object> addProductsToCart(HttpServletRequest request, List<CartRequest> cart) throws ProductException {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethods.role(request);
        System.out.println("---------------------------------------");
        System.out.println(user);
        Cart cart1 = cartRepo.findByUserIdAndStatus(user.getId(), "active");
        if (cart1 == null) {
            cart1 = new Cart(user.getId());
            cart1 = cartRepo.save(cart1);
        }

        if (cart1.getCartItems() == null) {
            cart1.setCartItems(new ArrayList<>());
        }


        // Process each product in the request
        for (CartRequest productRequest : cart) {
            // Validate if the product exists and check stock availability
            Products product;
            try {
                product = productRepo.findById(productRequest.getProductId()).get();
            } catch (RuntimeException e) {
                throw new ProductException("No product found");
            }
            System.out.println(product);
            if (product == null) {
                map.put("messsage", "Product not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }
            if (product.getStatus().equals("Not Avaliable")) {
                map.put("message", "Product Not Avaiable");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }
            if (product.getStock() < productRequest.getQuantity()) {
                map.put("message", "Insufficient stock");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

            // Check if the product is already in the cart
            CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cart1.getId(), productRequest.getProductId());
            if (cartItem == null) {
                // If not in the cart, add it
                cartItem = new CartItem(productRequest.getProductId(), productRequest.getQuantity());
                cartItem.setCart(cart1);
                cartItemRepo.save(cartItem);

                cart1.getCartItems().add(cartItem);
            } else {
                // If already in the cart, update the quantity
                cartItem.setQuantity(cartItem.getQuantity() + productRequest.getQuantity());
                cartItemRepo.save(cartItem);
            }
            int stock = product.getStock() - cartItem.getQuantity();
            if (stock <= 0) {
                product.setStatus(NOT_AVAILABLE);
                product.setStock(0);
                productRepo.save(product);
            } else {
                product.setStock(stock);
                productRepo.save(product);
            }
//            helperMethods.updateCartTotalAmount(cart1);
//            cart.updateTotalAmount(product);
//            cartRepository.save(cart);
        }

        // Update the total amount of the cart
        helperMethods.updateCartTotalAmount(cart1);
        System.out.println(cart1.getTotalAmount());
        cartRepo.save(cart1);
        map.put("message", "Item added to the cart");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> applyPromocode(String code, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethods.role(request);
        if (user == null) {
            map.put("message", "User Not Login");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            Optional<PromoCode> promoCode = promocodeRepo.findByCodeAndStatus(code, PromoCodeStatus.ACTIVE);
            if (!promoCode.isPresent()) {
                map.put("message", "No promo code is present or inactive");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

//            PromoCode promoCode = optionalPromoCode.get();
//
//            if (!promoCode.isValid()) {
//                map.put("message", "Promocode is inactive");
//                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//            }

            Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");
            if (cart == null) {
                map.put("message", "No cart found for the user " + user.getUsername());
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            } else {
                double totalDiscount = 0.0;

                if (promoCode.get().getType() == PromoCodeType.ORDER_BASED) {
                    // Apply the discount to the total amount of the cart
                    totalDiscount = promoCode.get().getDiscountValue();

                } else if (promoCode.get().getType() == PromoCodeType.PRODUCT_BASED) {
                    System.err.println("------------------------------------");
                    for (CartItem item : cart.getCartItems()) {
                        Optional<Products> productOpt = productRepo.findById(item.getProductId());
                        if (productOpt.isPresent()) {
                            Products product = productOpt.get();
                            if (product.getName().equals(promoCode.get().getProductName())) {
                                double discount = item.getQuantity() * product.getPrice() * promoCode.get().getDiscountValue() / 100;
                                totalDiscount += discount;
                                System.out.println(totalDiscount);
                            }
                        }
                    }
                }

                // Apply total discount to the cart
                cart.setTotalAmount(cart.getTotalAmount() - totalDiscount);
                PromoCode promoCode1 = promocodeRepo.findByCode(code);
                cart.setPromoCode(promoCode1);
                cartRepo.save(cart);

                map.put("message", "Promocode is applied successfully");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }
    }

//    @Transactional
//    public ResponseEntity<Object> checkouts(HttpServletRequest request) throws ProductException {
//        Map<String, Object> map = new HashMap<>();
//        Users user = helperMethods.role(request);
//        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");
//        Optional<PromoCode> promoCode = promocodeRepo.findById(cart.getPromoCode().getId());
//        if (cart == null) {
//            map.put("message", "No active cart found for the curent user");
//            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//        }
//
//        // Check if the user has an address
//        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
//            map.put("message", "Please add an address before proceeding with checkout");
//            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//        }
//
//        Wallet wallet = walletRepo.findByUserId(user.getId());
//        if (wallet == null) {
//            map.put("message", "No active wallet found for the current user");
//            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//        }
//
//
//        double finalAmount = cart.getTotalAmount();
//        // Check if the wallet has sufficient balance
//        if (wallet.getBalance() < cart.getTotalAmount()) {
//            Orders order = checkOutMethods.createOrder(user.getId(), cart, cart.getTotalAmount());
//            order.setOrderStatus("Failed");
//            orderRepository.save(order);
//            // Insufficient funds, log failed transaction and inform user
//            checkOutMethods.logFailedTransaction(user.getId(), cart.getTotalAmount(), order.getId());
//            map.put("message", "No sufficient balance in the Wallet");
//
//            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//        } else {
//
//            // Proceed with payment (deduct wallet balance)
//            double previousBalance = wallet.getBalance();
//            wallet.setBalance(wallet.getBalance() - cart.getTotalAmount());
//            walletRepo.save(wallet);
//
//            // Log wallet audit for the deduction
//            checkOutMethods.logWalletAudit(user.getId(), cart.getTotalAmount(), wallet.getBalance(), "payment");
//
//            // Create the order
//            Orders order = checkOutMethods.createOrder(user.getId(), cart, cart.getTotalAmount());
//            order.setOrderStatus("Paid");
//            orderRepository.save(order);
//
//            for (CartItem cartItem : cart.getCartItems()) {
//                OrderedItems orderedItem = new OrderedItems();
//                Optional<Products> products = productRepo.findById(cartItem.getProductId());
//                orderedItem.setQuantity(cartItem.getQuantity());
//                orderedItem.setName(products.get().getName());
//                Products products1 = productRepo.findById(cartItem.getProductId()).get();
//                if (products1.getName().equals(promoCode.get().getProductName())) {
//                    double discount = cartItem.getQuantity() * products1.getPrice() * promoCode.get().getDiscountValue() / 100;
//                    orderedItem.setDiscount(discount);
//                } else {
//                    orderedItem.setDiscount(0);
//                }
//                orderedItem.setPrice(products.get().getPrice() * cartItem.getQuantity());
//                orderedItem.setTotalAmount(orderedItem.getPrice() - orderedItem.getDiscount());
//                orderedItem.setTotalAmount(orderedItem.getTotalAmount() + orderedItem.getPrice());
//                orderedItem.setProduct(products.get());
//                orderedItem.setOrder(order); // Link the ordered item to the order
//
//                // Save the ordered item
//                orderedItemsRepo.save(orderedItem);
//            }
//
//            // Log order audit for the status change (from "Pending" to "Paid")
//            checkOutMethods.logOrderAudit(order.getId(), "PENDING", "PAID");
//
//            // Log a successful transaction and associate it with the order
//            try {
//                checkOutMethods.logSuccessfulTransaction(user.getId(), cart.getTotalAmount(), order.getId());
//            } catch (ProductException e) {
//                map.put("message", "order Not found");
//                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//            }
//            cart.setStatus("Completed");
//            cartRepo.save(cart);
//            map.put("message", "Order successfully");
//            return new ResponseEntity<>(map, HttpStatus.OK);
//        }
//
//
//    }

    @Transactional
    public ResponseEntity<Object> checkouts(HttpServletRequest request) throws ProductException {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethods.role(request);
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");

        // Check if cart is found
        if (cart == null) {
            map.put("message", "No active cart found for the current user");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        // Fetch the promo code if present
        Optional<PromoCode> promoCode = (cart.getPromoCode() != null) ? promocodeRepo.findById(cart.getPromoCode().getId()) : Optional.empty();

        // Check if the user has an address
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            map.put("message", "Please add an address before proceeding with checkout");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        Wallet wallet = walletRepo.findByUserId(user.getId());
        if (wallet == null) {
            map.put("message", "No active wallet found for the current user");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        double finalAmount = cart.getTotalAmount();

        // If a promo code is applied, apply the discount
//        if (promoCode.isPresent()) {
//            // Apply the promo code discount if it exists
//            finalAmount = applyPromoCodeDiscount(cart, promoCode.get());
//        }

        // Check if the wallet has sufficient balance
        if (wallet.getBalance() < finalAmount) {
            Orders order = checkOutMethods.createOrder(user.getId(), cart, finalAmount);
            order.setOrderStatus("Failed");
            orderRepository.save(order);
            checkOutMethods.logFailedTransaction(user.getId(), cart.getTotalAmount(), order.getId());
            map.put("message", "Insufficient balance in the wallet");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } else {
            // Proceed with payment (deduct wallet balance)
            wallet.setBalance(wallet.getBalance() - finalAmount);
            walletRepo.save(wallet);

            // Log wallet audit for the deduction
            checkOutMethods.logWalletAudit(user.getId(), finalAmount, wallet.getBalance(), "payment");

            // Create the order
            Orders order = checkOutMethods.createOrder(user.getId(), cart, finalAmount);
            order.setOrderStatus("Paid");
            orderRepository.save(order);

            // Process ordered items
            for (CartItem cartItem : cart.getCartItems()) {
                OrderedItems orderedItem = new OrderedItems();
                Optional<Products> products = productRepo.findById(cartItem.getProductId());
                orderedItem.setQuantity(cartItem.getQuantity());
                orderedItem.setName(products.get().getName());
                Products product = products.get();

                // If a promo code exists, apply the discount to the ordered items
                if (promoCode.isPresent() && product.getName().equals(promoCode.get().getProductName())) {
                    double discount = cartItem.getQuantity() * product.getPrice() * promoCode.get().getDiscountValue() / 100;
                    orderedItem.setDiscount(discount);
                } else {
                    orderedItem.setDiscount(0);
                }
                orderedItem.setPrice(product.getPrice() * cartItem.getQuantity());
                orderedItem.setTotalAmount(orderedItem.getPrice() - orderedItem.getDiscount());
                orderedItem.setProduct(product);
                orderedItem.setOrder(order); // Link the ordered item to the order

                // Save the ordered item
                orderedItemsRepo.save(orderedItem);
            }

            // Log order audit for status change (from "Pending" to "Paid")
            checkOutMethods.logOrderAudit(order.getId(), "PENDING", "PAID");

            // Log a successful transaction and associate it with the order
            try {
                checkOutMethods.logSuccessfulTransaction(user.getId(), cart.getTotalAmount(), order.getId());
            } catch (ProductException e) {
                map.put("message", "Order not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

            // Set the cart status to "Completed" and save
            cart.setStatus("Completed");
            cartRepo.save(cart);

            map.put("message", "Order placed successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

//    // Method to apply discount if promo code exists
//    private double applyPromoCodeDiscount(Cart cart, PromoCode promoCode) {
//        double discountAmount = 0.0;
//        if (promoCode != null && promoCode.getDiscountValue() > 0) {
//            discountAmount = cart.getTotalAmount() * promoCode.getDiscountValue() / 100;
//        }
//        return cart.getTotalAmount() - discountAmount;
//    }


    @Transactional
    public ResponseEntity<Object> cancleorders(int orderId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            Users user = helperMethods.role(request);
            if (user.getRole().getName().equals("USER")) {
                Orders order = orderRepository.findById(orderId).orElse(null);
                if (order == null) {
                    map.put("message", "Order not found");
                    return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
                }

                // Check if the order belongs to the logged-in user
                if (order.getUserId() != user.getId()) {
                    map.put("message", "This order does not belong to the logged-in user");
                    return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
                }

                // Check if the order is already completed
                if (order.getOrderStatus().equalsIgnoreCase("completed")) {
                    map.put("message", "Order delivery completed. You cannot cancel it");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }

                // Ensure promo code orders cannot be canceled
                if (order.getPromoCode() != null) {
                    map.put("message", "Promo code applied orders cannot be canceled");
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }

                // Update the user's wallet balance
                Wallet wallet = walletRepo.findByUserId(user.getId());
                wallet.setBalance(wallet.getBalance() + order.getTotalPrice());
                walletRepo.save(wallet);
                // Create a wallet audit record
                WalletAudit walletAudit = new WalletAudit();
                walletAudit.setBalanceAfterTransaction(wallet.getBalance());
                walletAudit.setAmount(order.getTotalPrice());
                walletAudit.setTransactionType("Refound");
                walletAudit.setUserId(wallet.getUser().getId());
                walletAuditRepo.save(walletAudit);

                // Change order status to "Cancelled"
                order.setOrderStatus("Cancelled");

//                // Update the order status in the order status table
//                OrderStatus orderStatus = orderStatusRepo.findByOrder(order);
//                orderStatus.setStatus("Cancelled");

                // Create a new order audit record
                List<OrderAudit> orderedAuditList = orderAudictRepository.findAllByOrderId(order.getId());
                OrderAudit lastOrderedAudit = orderedAuditList.get(orderedAuditList.size() - 1);

                OrderAudit orderedAudit = new OrderAudit();
                orderedAudit.setNewStatus("Cancelled");
                orderedAudit.setPreviousStatus(lastOrderedAudit.getNewStatus());
                orderedAudit.setOrderId(order.getId());

                // Restore the stock of the products
                List<OrderedItems> orderedItemsList = orderedItemsRepo.findAllByOrder(order);
                for (OrderedItems orderedItem : orderedItemsList) {
                    int productId = orderedItem.getProduct().getId();
                    Products product = productRepo.findById(productId).orElse(null);
                    if (product != null) {
                        product.setStock(product.getStock() + orderedItem.getQuantity());
                        product.setStatus(AVAILABLE);
                        productRepo.save(product);
                    }
                }

                Transaction transaction = new Transaction();
                transaction.setUserId(user.getId());
                transaction.setAmount(order.getTotalPrice());
                transaction.setStatus("Cancelled");
                transaction.setOrder(order);
                transaction.setTimestamp(LocalDateTime.now());
                transactionRepository.save(transaction);

                // Save all the updated records (order, order status, audits)
                orderAudictRepository.save(orderedAudit);
//                orderStatusRepo.save(orderStatus);
                orderRepository.save(order);

                return new ResponseEntity<>("Order cancelled successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> address(AddressDto addressRequest, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            Users user = helperMethods.role(request);
            System.out.println("-------------------------------");
            System.out.println(user.getRole().getName());
            if (user.getRole().getName().equals("USER")) {
                Address address = new Address();
                address.setStreet(addressRequest.getStreet());
                address.setCity(addressRequest.getCity());
                address.setState(addressRequest.getState());
                address.setCountry(addressRequest.getCountry());
                address.setPostalCode(addressRequest.getPostalCode());
                address.setUser(user); // Associate address with user
                addressRepo.save(address);
                map.put("message", "Address added successfull");
                map.put("data", addressRequest);
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> updateaddress(AddressDto address, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Fetch the logged-in user using the helper method
            Users user = helperMethods.role(request);

            // Ensure the user has the "USER" role
            if (!user.getRole().getName().equals("USER")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // Fetch the user's current address (assuming user.getAddress() gets the address object)
            Address existingAddress = (Address) user.getAddresses();

            // If the user doesn't have an address, return a 404 not found response
            if (existingAddress == null) {
                map.put("message", "Address not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

            // Update the fields of the address (only if they are provided)
            if (address.getStreet() != null) {
                existingAddress.setStreet(address.getStreet());
            }
            if (address.getCity() != null) {
                existingAddress.setCity(address.getCity());
            }
            if (address.getState() != null) {
                existingAddress.setState(address.getState());
            }
            if (address.getCountry() != null) {
                existingAddress.setCountry(address.getCountry());
            }
            if (address.getPostalCode() != null) {
                existingAddress.setPostalCode(address.getPostalCode());
            }

            // Save the updated address
            addressRepo.save(existingAddress);

            // Prepare the response message
            map.put("message", "Address updated successfully");
            map.put("data", existingAddress);
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception e) {
            map.put("message", "An error occurred while updating the address");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
}


