package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Orders;
import org.hibernate.query.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Integer> {
    List<Orders> findByOrderStatus(String completed);

    List<Orders> findByUserId(int id);

    Page<Orders> findByOrderStatus(String completed, Pageable pageable);
}
