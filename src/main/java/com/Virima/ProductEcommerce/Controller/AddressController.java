package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Service.AddressService;
import com.Virima.ProductEcommerce.dto.AddressDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddressController {

    @Autowired
    AddressService addressService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/address")
    public ResponseEntity<Object> address(@RequestBody AddressDto address, HttpServletRequest request)
    {
        return addressService.address(address,request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/updateaddress/{id}")
    public ResponseEntity<Object> updateaddress(@PathVariable Long id,@RequestBody AddressDto address, HttpServletRequest request)
    {
        return addressService.updateaddress(id,address,request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/fetchaddress")
    public ResponseEntity<Object> fetchAddress(HttpServletRequest request)
    {
        return addressService.fetchAddress(request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/fetchaddress")
    public ResponseEntity<Object> deleteAddress(@RequestParam Long addressId, HttpServletRequest request)
    {
        return addressService.deleteAddress(addressId,request);
    }

}
