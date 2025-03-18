package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Service.PromoCodeService;
import com.Virima.ProductEcommerce.dto.PromoCodeDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class PromoCodeController {

    @Autowired
    PromoCodeService promoCodeService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/promocode")
    public ResponseEntity<Object> createPromoCode(@RequestBody PromoCodeDto promoCodeDto) {

        return promoCodeService.createPromoCode(promoCodeDto);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/promocode/{code}")
    public ResponseEntity<Object> UpdatePromoCode(@PathVariable String code , @RequestBody PromoCodeDto promoCodeDto) {

        return promoCodeService.UpdatePromoCode(code,promoCodeDto);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/promocode/{code}")
    public ResponseEntity<Object> fetchPromoCode(@PathVariable String code ) {

        return promoCodeService.fetchPromoCode(code);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/promocode")
    public ResponseEntity<Object> fetchAllPromoCode( ) {

        return promoCodeService.fetchAllPromoCode();

    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/promocode/{code}")
    public ResponseEntity<Object> applyPromocode(@PathVariable String code, HttpServletRequest request)
    {
        return promoCodeService.applyPromocode(code,request);
    }
}
