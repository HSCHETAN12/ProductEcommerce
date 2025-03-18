package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Cart extends TrackingColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private double totalAmount;

    private String status ;

    @OneToMany(mappedBy = "cart", orphanRemoval = true)
    private List<CartItem> cartItems;

    public Cart(int userId) {
        this.userId = userId;
        this.totalAmount = 0.0;
        this.status="active";
    }

    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;


    public void updateTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
