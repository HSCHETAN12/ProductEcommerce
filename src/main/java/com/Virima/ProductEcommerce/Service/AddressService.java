package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.dto.AddressDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<Object> address(AddressDto address, HttpServletRequest request);

    ResponseEntity<Object> updateaddress(Long id,AddressDto address, HttpServletRequest request);

    ResponseEntity<Object> fetchAddress(HttpServletRequest request);

    ResponseEntity<Object> deleteAddress(Long addressId,HttpServletRequest request);
}
