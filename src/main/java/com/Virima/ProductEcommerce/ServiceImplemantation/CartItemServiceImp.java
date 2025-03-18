package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Cart;
import com.Virima.ProductEcommerce.Entity.CartItem;
import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.CartItemRepo;
import com.Virima.ProductEcommerce.Repo.CartRepo;
import com.Virima.ProductEcommerce.Service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartItemServiceImp implements CartItemService {

    @Autowired
    HelperMethods helperMethods;

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    CartRepo cartRepo;
    /**
     * Soft deletes a product (CartItem) from the user's active cart.
     *
     * 1. Retrieves the user's active cart.
     * 2. Verifies the CartItem exists and belongs to the user's cart.
     * 3. Marks the CartItem as deleted (soft delete).
     * 4. Recalculates the cart's total amount.
     * 5. Saves the updated CartItem and cart.
     *
     * @param request The HttpServletRequest object containing user-related information.
     * @param cartItemId The ID of the CartItem to be soft deleted.
     * @return A ResponseEntity with a message and HTTP status code.
     */
    public ResponseEntity<Object> softDeleteProductFromCart(HttpServletRequest request, int cartItemId) throws ProductException {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethods.role(request);

        // Find the active cart for the user
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");
        if (cart == null) {
//            map.put("message", "Cart not found for the user");
//            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            throw new ProductException( "Cart not found for the user");
        }


        CartItem cartItem = cartItemRepo.findByIdAndIsDeletedFalse(cartItemId);
        if (cartItem == null) {
//            map.put("message", "Cart item not found");
//            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            throw new ProductException( "Cart item not found");
        }

        // Check if the CartItem belongs to the user's cart
        if (cartItem.getCart().getId()!=(cart.getId())) {
//            map.put("message", "This item does not belong to your cart");
//            return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
            throw new ProductException( "This item does not belong to your cart");
        }


        cartItem.setDeleted(true);


        cartItemRepo.save(cartItem);


        helperMethods.updateCartTotalAmount(cart);


        cartRepo.save(cart);

        map.put("message", "Item soft deleted from the cart successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
