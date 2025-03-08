package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class WalletAudit extends TrackingColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int userId;  // User ID who made the transaction
    private String transactionType; // DEBIT or CREDIT
    private double amount;  // Amount added or deducted
    private double balanceAfterTransaction;  // Balance after the transaction
}
