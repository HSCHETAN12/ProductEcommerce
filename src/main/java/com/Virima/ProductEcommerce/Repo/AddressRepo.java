package com.Virima.ProductEcommerce.Repo;

import com.Virima.ProductEcommerce.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address,Long> {


    List<Address> findByUserId(int id);
}
