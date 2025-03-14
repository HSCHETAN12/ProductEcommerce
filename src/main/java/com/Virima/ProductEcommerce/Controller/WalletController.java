package com.Virima.ProductEcommerce.Controller;

import com.Virima.ProductEcommerce.Service.WalletService;
import com.Virima.ProductEcommerce.dto.WalletTopUpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class WalletController {

    @Autowired
    WalletService walletService;


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/wallettopup")
    public ResponseEntity<Object> WalletTopUp(@RequestBody WalletTopUpDto walletTopUpRequest) {
        return walletService.WalletTopUp(walletTopUpRequest);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/wallett/{userId}")
    public ResponseEntity<Object> fetchWallet(@PathVariable int userId) {
        return walletService.fetchWallet(userId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/wallett/{userId}")
    public ResponseEntity<Object> DeleteWallet(@PathVariable int userId) {
        return walletService.DeleteWallet(userId);
    }
}
