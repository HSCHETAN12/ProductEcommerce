package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart,Integer> {

    Cart findByUserIdAndStatus(int id, String active);

    Cart findByUserId(int id);
}
