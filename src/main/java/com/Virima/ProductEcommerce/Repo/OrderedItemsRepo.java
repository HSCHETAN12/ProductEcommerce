package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.OrderedItems;
import com.Virima.ProductEcommerce.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderedItemsRepo extends JpaRepository<OrderedItems,Integer> {
    List<OrderedItems> findAllByOrder(Orders orders);
}
