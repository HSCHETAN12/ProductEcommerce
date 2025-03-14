package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderAudictRepository extends JpaRepository<OrderAudit,Integer> {
    List<OrderAudit> findAllByOrderId(int id);
}
