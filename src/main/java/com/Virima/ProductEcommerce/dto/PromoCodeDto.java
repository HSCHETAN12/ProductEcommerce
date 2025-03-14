package com.Virima.ProductEcommerce.dto;

import com.Virima.ProductEcommerce.Entity.PromoCodeStatus;
import com.Virima.ProductEcommerce.Entity.PromoCodeType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PromoCodeDto {

    private String code;
    private Double discountValue;
    private PromoCodeType type;
    private Date startDate;
    private Date endDate;
    private PromoCodeStatus status;
    private String productName;
}
