package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.PromoCode;
import com.Virima.ProductEcommerce.Entity.PromoCodeStatus;
import com.Virima.ProductEcommerce.Entity.PromoCodeType;
import com.Virima.ProductEcommerce.Repo.PromocodeRepo;
import com.Virima.ProductEcommerce.Service.PromoCodeService;
import com.Virima.ProductEcommerce.dto.PromoCodeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class PromoCodeServiceImp implements PromoCodeService {

    @Autowired
    PromocodeRepo promocodeRepo;

    public ResponseEntity<Object> createPromoCode(PromoCodeDto promoCodeDto) {
        Map<String, Object> map = new HashMap<>();
        Optional<PromoCode> promoCode = promocodeRepo.findByCodeAndStatus(promoCodeDto.getCode(), PromoCodeStatus.ACTIVE);
        System.out.println(promoCode);
        if (!promoCode.isEmpty()) {
            map.put("message", "Promocode Exists in the database");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            PromoCode promoCode1 = new PromoCode(promoCodeDto.getCode(), promoCodeDto.getDiscountValue(), promoCodeDto.getType(), promoCodeDto.getStartDate(), promoCodeDto.getEndDate(), promoCodeDto.getStatus(), promoCodeDto.getProductName());
            if (promoCodeDto.getType().equals(PromoCodeType.ORDER_BASED)) {
                promoCode1.setProductName(null);
            }
            promocodeRepo.save(promoCode1);
            map.put("message", "Promocode Added successfully");
            map.put("data", promoCode);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> UpdatePromoCode(String code, PromoCodeDto promoCodeDto) {
        Map<String, Object> map = new HashMap<>();

        // Check if the promo code exists
        Optional<PromoCode> existingPromoCode = promocodeRepo.findByCodeAndStatus(code, PromoCodeStatus.ACTIVE);

        if (existingPromoCode.isPresent()) {
            PromoCode promoCodeToUpdate = existingPromoCode.get();

            // Update fields that are not null in the DTO
            if (promoCodeDto.getDiscountValue() != null) {
                promoCodeToUpdate.setDiscountValue(promoCodeDto.getDiscountValue());
            }
            if (promoCodeDto.getType() != null) {
                promoCodeToUpdate.setType(promoCodeDto.getType());
            }
            if (promoCodeDto.getStartDate() != null) {
                promoCodeToUpdate.setStartDate(promoCodeDto.getStartDate());
            }
            if (promoCodeDto.getEndDate() != null) {
                promoCodeToUpdate.setEndDate(promoCodeDto.getEndDate());
            }
            if (promoCodeDto.getStatus() != null) {
                promoCodeToUpdate.setStatus(promoCodeDto.getStatus());
            }
            if (promoCodeDto.getProductName() != null) {
                promoCodeToUpdate.setProductName(promoCodeDto.getProductName());
            }
            // If the promo code type is ORDER_BASED, set productName to null
            if (promoCodeDto.getType().equals(PromoCodeType.ORDER_BASED)) {
                promoCodeToUpdate.setProductName(null);
            }

            // Save the updated promo code
            promocodeRepo.save(promoCodeToUpdate);

            map.put("message", "Promo code updated successfully");
            map.put("data", promoCodeToUpdate);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("message", "Promo code not found or inactive");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> fetchPromoCode(String code) {
        Map<String, Object> map = new HashMap<>();
        PromoCode promoCode=promocodeRepo.findByCode(code);
        if(promoCode==null)
        {
            map.put("message","No promocode found");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }else{
            map.put("data",promoCode);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchAllPromoCode() {
        Map<String, Object> map = new HashMap<>();
        List<PromoCode> promoCodes=promocodeRepo.findAll();
        if(promoCodes==null)
        {
            map.put("message","No promocode found");
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }else{
            map.put("data",promoCodes);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }


}

