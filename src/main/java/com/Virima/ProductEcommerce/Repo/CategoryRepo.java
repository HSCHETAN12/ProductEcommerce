package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category,Integer> {

    Category findByName(String category);
}
