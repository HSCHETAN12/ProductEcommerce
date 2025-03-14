package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Orders;
import org.hibernate.query.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Integer> {
    List<Orders> findByOrderStatus(String paid);

    List<Orders> findByUserId(int id);
}
