package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class Users extends TrackingColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private Long mobile;
    private String password;
    @Transient
    private String confirmpassword;
    private String gender;
    private int otp;
    private boolean verified;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private Wallet wallet; // One-to-One relationship with wallet


    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference  // Prevents serialization of the reverse relationship
    private Role role;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Address> addresses;
}
