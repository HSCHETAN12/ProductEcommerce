package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.dto.PromoCodeDto;
import org.springframework.http.ResponseEntity;

public interface PromoCodeService {
    ResponseEntity<Object> createPromoCode(PromoCodeDto promoCodeDto);

    ResponseEntity<Object> UpdatePromoCode(String code,PromoCodeDto promoCodeDto);

    ResponseEntity<Object> fetchPromoCode(String code);

    ResponseEntity<Object> fetchAllPromoCode();
}
