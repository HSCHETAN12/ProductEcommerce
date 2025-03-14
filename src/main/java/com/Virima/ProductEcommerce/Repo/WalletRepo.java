package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet,Integer> {
    Wallet findByUserId(int userId);

    Wallet findByUserIdAndIsDeletedFalse(int id);
}
