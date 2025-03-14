package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Users;
import jakarta.validation.constraints.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepo extends JpaRepository<Users,Integer> {
    Users findByusername(String username);

    boolean existsByEmail(@Email(message = "It should be proper Email format") @NotEmpty(message = "It is required Field") String email);

    boolean existsByMobile(@DecimalMin(value = "6000000000", message = "It should be proper mobile number") @DecimalMax(value = "9999999999", message = "It should be proper mobile number") long mobile);

    boolean existsByUsername(@Size(min = 5, max = 15, message = "It should be between 5 and 15 charecters") String username);

    @Query("SELECT u FROM Users u WHERE u.verified = true AND u.isDeleted = false")
    List<Users> findVerifiedAndNotDeleted();
}
