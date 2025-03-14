package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.dto.WalletTopUpDto;
import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<Object> WalletTopUp(WalletTopUpDto walletTopUpRequest);

    ResponseEntity<Object> fetchWallet( int userId);

    ResponseEntity<Object> DeleteWallet(int userId);
}
