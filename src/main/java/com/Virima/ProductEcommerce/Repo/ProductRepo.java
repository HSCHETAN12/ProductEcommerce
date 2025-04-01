package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Category;
import com.Virima.ProductEcommerce.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Products,Integer> {
    List<Products> findByIsDeletedFalseAndStockGreaterThan(int i);

    List<Products> findByCategory_NameAndIsDeletedFalseAndStockGreaterThan(String categoryName, int stock);



    Products findByNameAndIsDeletedFalseAndStockGreaterThan(String name, int stock);

    Optional<Products> findByIdAndIsDeletedFalse(int id);

    List<Products> findByCategoryAndIsDeletedFalse(Category categoryOpt);
}
