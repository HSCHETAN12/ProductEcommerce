package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.PromoCode;
import com.Virima.ProductEcommerce.Entity.PromoCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PromocodeRepo extends JpaRepository<PromoCode,Integer> {



    @Query("SELECT p FROM PromoCode p WHERE p.code = :code AND p.status = :status")
    Optional<PromoCode> findByCodeAndStatus(@Param("code") String code, @Param("status") PromoCodeStatus status);

    PromoCode findByCode(String code);
}
