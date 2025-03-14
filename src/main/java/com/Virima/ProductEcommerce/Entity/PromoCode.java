package com.Virima.ProductEcommerce.Entity;

import com.Virima.ProductEcommerce.Base.TrackingColumn;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public class PromoCode extends TrackingColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;
    private Double discountValue;
    @Enumerated(EnumType.STRING)
    private PromoCodeType type;

    private Date startDate;
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private PromoCodeStatus status;

    private String productName;


    public PromoCode(String code, Double discountValue, PromoCodeType type,
                     Date startDate, Date endDate,
                     PromoCodeStatus status, String productName) {
        this.code = code;
        this.discountValue = discountValue;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.productName = productName;
    }
}
