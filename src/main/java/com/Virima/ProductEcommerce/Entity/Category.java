package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Category extends TrackingColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String name;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<Products> products;
}
