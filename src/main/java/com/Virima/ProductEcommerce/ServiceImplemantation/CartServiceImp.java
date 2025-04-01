package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Cart;
import com.Virima.ProductEcommerce.Entity.CartItem;
import com.Virima.ProductEcommerce.Entity.Products;
import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.CartItemRepo;
import com.Virima.ProductEcommerce.Repo.CartRepo;
import com.Virima.ProductEcommerce.Repo.ProductRepo;
import com.Virima.ProductEcommerce.Service.CartService;
import com.Virima.ProductEcommerce.dto.CartDto;
import com.Virima.ProductEcommerce.dto.CartItemDto;
import com.Virima.ProductEcommerce.dto.CartRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.Virima.ProductEcommerce.Entity.ProductStatus.AVAILABLE;
import static com.Virima.ProductEcommerce.Entity.ProductStatus.NOT_AVAILABLE;

@Service
public class CartServiceImp implements CartService {

    @Autowired
    CartItemRepo cartItemRepo;

    @Autowired
    HelperMethods helperMethodsa;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ProductRepo productRepo;

    public ResponseEntity<Object> addProductsToCart(HttpServletRequest request, List<CartRequest> cart) throws ProductException {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethodsa.role(request);
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


        for (CartRequest productRequest : cart) {

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


            CartItem cartItem = cartItemRepo.findByCartIdAndProductId(cart1.getId(), productRequest.getProductId());
            if (cartItem == null) {
                // If not in the cart, add it
                cartItem = new CartItem(productRequest.getProductId(), productRequest.getQuantity());
                cartItem.setCart(cart1);
                cartItemRepo.save(cartItem);
                cart1.getCartItems().add(cartItem);
            } else {
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
        helperMethodsa.updateCartTotalAmount(cart1);
        System.out.println(cart1.getTotalAmount());
        cartRepo.save(cart1);
        map.put("message", "Item added to the cart");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Object> DeleteCartItems(int productId, HttpServletRequest request) throws ProductException {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethodsa.role(request);
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");
        if (cart == null) {
            throw new ProductException("Cart not found");
        } else {
            CartItem cartItem = cart.getCartItems()
                    .stream().filter(item -> item.getProductId() == productId)
                    .findFirst().orElseThrow(() -> new RuntimeException("Product not in cart"));

            Products product = productRepo.findById(productId)
                    .orElseThrow(() -> new ProductException("Product not found"));

            int updatedStock = product.getStock() + cartItem.getQuantity();
            product.setStatus(AVAILABLE);
            product.setStock(updatedStock);
            productRepo.save(product);

            cart.updateTotalAmount(cart.getTotalAmount() - (cartItem.getQuantity() * productRepo.findById(productId).get().getPrice()));


            cart.getCartItems().remove(cartItem);
            cartItemRepo.delete(cartItem);
            cartRepo.save(cart);
            map.put("message", "product removed from the cart");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchCart(HttpServletRequest request) throws ProductException {
        Map<String, Object> map = new HashMap<>();

        // Get the user from the request
        Users user = helperMethodsa.role(request);

        // Find the active cart for the user
        Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");

        // If no active cart exists, return an error response
        if (cart == null) {
            map.put("message", "Cart not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        // Create a CartDto and set the Cart properties
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setUserId(cart.getUserId());
        cartDto.setTotalAmount(cart.getTotalAmount());
        cartDto.setStatus(cart.getStatus());

        // Convert CartItems to CartItemDto and fetch product details
        List<CartItemDto> cartItemDtos = cart.getCartItems().stream().map(cartItem -> {
            CartItemDto cartItemDto = new CartItemDto();
            cartItemDto.setId(cartItem.getId());
            cartItemDto.setProductId(cartItem.getProductId());
            cartItemDto.setQuantity(cartItem.getQuantity());

            // Fetch product details (name & image)
            Products product = productRepo.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                cartItemDto.setProductName(product.getName());
                cartItemDto.setImageUrl(product.getImageUrl());
            } else {
                cartItemDto.setProductName("Unknown Product");  // Fallback if product not found
                cartItemDto.setImageUrl("https://via.placeholder.com/150");
            }

            return cartItemDto;
        }).collect(Collectors.toList());

        cartDto.setCartItems(cartItemDtos);

        // Return the cart details with product info
        map.put("data", cartDto);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
