package com.Virima.ProductEcommerce.Helper;

import com.Virima.ProductEcommerce.Entity.*;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
@Component
public class checkOutMethods {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WalletAuditRepo walletAuditRepository;

    @Autowired
    OrderAudictRepository orderAudictRepository;

    @Autowired
    ProductRepo productRepository;


    public Orders createOrder(int userId, Cart cart, double totalPrice) {
        // Create an order object based on cart and user details
        Orders order = new Orders();
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        if(cart.getPromoCode()!=null)
        {
            order.setPromoCode(cart.getPromoCode());
        }else{
            order.setPromoCode(null);
        }
        order.setCart(cart);
        return order;
    }

    public void logFailedTransaction(int userId, double amount, int orderId) throws ProductException {
        // Log the failed transaction in the Transaction table
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setStatus("FAILED");
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ProductException("Order not found"));
        transaction.setOrder(order);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public void logSuccessfulTransaction(int userId, double amount, int orderId) throws ProductException {
        // Log the successful transaction in the Transaction table, associating it with the order
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setStatus("SUCCESS");
        transaction.setTimestamp(LocalDateTime.now());

        // Fetch order by ID and associate with the transaction
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ProductException("Order not found"));
        transaction.setOrder(order);

        transactionRepository.save(transaction);
    }

    public void logWalletAudit(int userId, double amount, double newBalance, String transactionType) {
        // Create a WalletAudit entry to track the wallet transaction
        WalletAudit walletAudit = new WalletAudit();
        walletAudit.setUserId(userId);
        walletAudit.setAmount(amount);
        walletAudit.setBalanceAfterTransaction(newBalance);
        walletAudit.setTransactionType(transactionType);

        walletAuditRepository.save(walletAudit);
    }

    public void logOrderAudit(int orderId, String previousStatus, String newStatus) {
        // Create an OrderAudit entry to track the order status change
        OrderAudit orderAudit = new OrderAudit();
        orderAudit.setOrderId(orderId);
        orderAudit.setPreviousStatus(previousStatus);
        orderAudit.setNewStatus(newStatus);

        orderAudictRepository.save(orderAudit);
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
                        product = productRepository.findById(item.getProductId())
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
}
