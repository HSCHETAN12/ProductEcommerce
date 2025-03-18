package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.dto.WalletTopUpDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<Object> WalletTopUp(WalletTopUpDto walletTopUpRequest);

    ResponseEntity<Object> fetchWallet( int userId);

    ResponseEntity<Object> DeleteWallet(int userId);

    ResponseEntity<Object> fetchWalletAudict(int id, HttpServletRequest request);
}
