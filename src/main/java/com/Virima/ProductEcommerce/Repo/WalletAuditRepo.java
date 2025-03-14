package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.WalletAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletAuditRepo extends JpaRepository<WalletAudit,Integer> {
    List<WalletAudit> findByUserId(int id);
}
