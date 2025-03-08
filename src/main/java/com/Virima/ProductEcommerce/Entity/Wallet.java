package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Wallet extends TrackingColumn{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double balance;

    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private Users user;
}
