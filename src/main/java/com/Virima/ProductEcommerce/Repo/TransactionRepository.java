package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
}
