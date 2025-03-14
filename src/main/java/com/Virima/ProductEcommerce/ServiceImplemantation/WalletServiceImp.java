package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Wallet;
import com.Virima.ProductEcommerce.Entity.WalletAudit;
import com.Virima.ProductEcommerce.Repo.WalletAuditRepo;
import com.Virima.ProductEcommerce.Repo.WalletRepo;
import com.Virima.ProductEcommerce.Service.WalletService;
import com.Virima.ProductEcommerce.dto.WalletTopUpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class WalletServiceImp implements WalletService {
    @Autowired
    WalletRepo walletRepo;

    @Autowired
    WalletAuditRepo walletAuditRepo;

    public ResponseEntity<Object> WalletTopUp(WalletTopUpDto walletTopUpRequest) {
        Map<String, Object> map = new HashMap<>();
        Wallet wallet = walletRepo.findByUserId(walletTopUpRequest.getUserId());
        if (wallet == null) {
            map.put("message", "User's wallet not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        double newBalance = wallet.getBalance() + walletTopUpRequest.getAmount();
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);

        WalletAudit walletAudit = new WalletAudit();
        walletAudit.setUserId(walletTopUpRequest.getUserId());
        walletAudit.setTransactionType("CREDIT");
        walletAudit.setAmount(walletTopUpRequest.getAmount());
        walletAudit.setBalanceAfterTransaction(newBalance);
        walletAuditRepo.save(walletAudit);
        map.put("message", "Wallet topup done to given user id");
        map.put("data", walletTopUpRequest);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Object> fetchWallet(int id) {

        Map<String, Object> map = new HashMap<>();
        Wallet wallet = walletRepo.findByUserIdAndIsDeletedFalse(id);
        if (wallet == null) {
            map.put("message", "User's wallet not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            map.put("data", wallet);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }

    }

    public ResponseEntity<Object> DeleteWallet(int userId) {
        Map<String, Object> map = new HashMap<>();
        Wallet wallet = walletRepo.findByUserIdAndIsDeletedFalse(userId);
        if (wallet == null) {
            map.put("message", "User's wallet not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            wallet.setDeleted(true);
            walletRepo.save(wallet);
            map.put("message","User Wallet deleted");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }


}
