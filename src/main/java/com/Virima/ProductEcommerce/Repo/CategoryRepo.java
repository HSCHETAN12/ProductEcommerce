package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Category;
import com.Virima.ProductEcommerce.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category,Integer> {

    Category findByName(String category);

}
