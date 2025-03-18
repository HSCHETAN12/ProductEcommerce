package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.*;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Helper.checkOutMethods;
import com.Virima.ProductEcommerce.Repo.*;
import com.Virima.ProductEcommerce.Service.OrdersService;
import com.Virima.ProductEcommerce.dto.OrdersDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.Virima.ProductEcommerce.Entity.ProductStatus.AVAILABLE;

@Service
public class OrdersServiceImp implements OrdersService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    HelperMethods helperMethods;

    @Autowired
    PromocodeRepo promocodeRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    private com.Virima.ProductEcommerce.Helper.checkOutMethods checkOutMethods;

    @Autowired
    WalletAuditRepo walletAuditRepo;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    OrderAudictRepository orderAudictRepository;

    @Autowired
    OrderedItemsRepo orderedItemsRepo;


    /**
     * Fetches a paginated list of orders based on their status.
     *
     * 1. If the status is not provided, defaults to "completed".
     * 2. Retrieves orders based on the specified status and pagination parameters (page, size).
     * 3. Supports the following order statuses: "completed", "paid", "dispatched", "cancelled".
     * 4. Returns a 400 (BAD_REQUEST) if an invalid status is provided.
     * 5. If no orders are found for the given status, returns a 404 (NOT_FOUND).
     * 6. Maps the orders to a list of OrdersDto and returns a 200 (OK) response with order data,
     *    current page, total items, and total pages.
     *
     * @param request The HttpServletRequest object (unused in this method but typically contains user info).
     * @param status The status of the orders to fetch (e.g., "completed", "paid").
     * @param page The page number for pagination.
     * @param size The number of orders per page for pagination.
     * @return A ResponseEntity with order data, pagination info, and HTTP status code.
     */
    public ResponseEntity<Object> fetchOrders(HttpServletRequest request, String status, int page, int size) {
        Map<String, Object> map = new HashMap<>();
        if (status == null) {
            status = "completed";
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<Orders> orders;
        switch (status.toLowerCase()) {
            case "completed":
                orders = orderRepository.findByOrderStatus("completed", pageable);
                break;
            case "paid":
                orders = orderRepository.findByOrderStatus("paid", pageable);
                break;
            case "dispatched":
                orders = orderRepository.findByOrderStatus("dispatched", pageable);
                break;
            case "cancelled":
                orders = orderRepository.findByOrderStatus("cancelled", pageable);
//                System.out.println(orders.getContent());
                break;
            default:
                map.put("message", "Invalid status");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (orders.isEmpty()) {
            map.put("message", "No orders found for status: " + status);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        List<OrdersDto> ordersDtos = orders.getContent().stream().map(order -> {
            OrdersDto ordersDto = new OrdersDto();
            ordersDto.setId(order.getId());
            ordersDto.setUserId(order.getUserId());
            ordersDto.setTotalPrice(order.getTotalPrice());
            ordersDto.setOrderStatus(order.getOrderStatus());

            // Set cartId and promoCodeId if they exist
            if (order.getCart() != null) {
                ordersDto.setCartId(order.getCart().getId());
            }
            if (order.getPromoCode() != null) {
                ordersDto.setPromoCodeId(order.getPromoCode().getId());
            }

            return ordersDto;
        }).collect(Collectors.toList());

        map.put("data", ordersDtos );
        map.put("currentPage", orders.getNumber());
        map.put("totalItems", orders.getTotalElements());
        map.put("totalPages", orders.getTotalPages());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    /**
     * Handles the checkout process for the user's active cart.
     *
     * 1. Verifies if the user is logged in and retrieves their active cart; if no active cart is found, returns an error.
     * 2. Checks if the user has an address and wallet; returns an error if either is missing.
     * 3. If a promo code is applied, the discount is considered when calculating the final amount.
     * 4. Checks if the wallet has sufficient balance to cover the final amount; if not, a failed order is created.
     * 5. If the wallet has enough balance:
     *    - Deducts the balance from the wallet.
     *    - Creates a new order and processes each cart item (including potential discounts for promo codes).
     *    - Saves the ordered items and updates the cart status to "Completed."
     * 6. Logs successful transactions and order status changes for auditing purposes.
     *
     * @param request The HTTP request containing user authentication details.
     * @return A ResponseEntity with the status of the checkout process and an appropriate HTTP status code.
     */

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

    /**
     * Cancels an order placed by the logged-in user if certain conditions are met.
     *
     * 1. Verifies if the logged-in user has the "USER" role and retrieves the order by its ID.
     * 2. Ensures that the order exists and belongs to the logged-in user; returns an error if not.
     * 3. Checks if the order is already completed or if a promo code was applied; such orders cannot be canceled.
     * 4. Updates the user's wallet balance by refunding the total price of the canceled order.
     * 5. Creates a wallet audit record for the refund transaction.
     * 6. Changes the order status to "Cancelled" and creates an order audit record for the status change.
     * 7. Restores the stock of the ordered products and sets their status back to "Available".
     * 8. Logs a new transaction for the canceled order and saves it in the transaction history.
     * 9. Finally, saves the updated records for the order, order status, and audit entries.
     *
     * @param orderId The ID of the order to be canceled.
     * @param request The HTTP request containing user authentication details.
     * @return A ResponseEntity indicating the status of the cancellation request and an appropriate HTTP status code.
     */

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
}
