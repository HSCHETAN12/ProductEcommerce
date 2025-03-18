package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartItemRepo extends JpaRepository<CartItem,Integer> {

    CartItem findByCartIdAndProductId(int id, int productId);

    CartItem findByIdAndIsDeletedFalse(int cartItemId);
}
