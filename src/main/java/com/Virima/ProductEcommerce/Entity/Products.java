package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Products extends TrackingColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;


    private double price;


    private int stock;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ProductStatus status ;
}
