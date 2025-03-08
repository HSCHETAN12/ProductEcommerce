package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Products,Integer> {
    List<Products> findByIsDeletedFalseAndStockGreaterThan(int i);

    List<Products> findByCategory(String category);
}
