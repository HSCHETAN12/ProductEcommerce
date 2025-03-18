package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Wallet;
import com.Virima.ProductEcommerce.Entity.WalletAudit;
import com.Virima.ProductEcommerce.Repo.WalletAuditRepo;
import com.Virima.ProductEcommerce.Repo.WalletRepo;
import com.Virima.ProductEcommerce.Service.WalletService;
import com.Virima.ProductEcommerce.dto.WalletTopUpDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class WalletServiceImp implements WalletService {
    @Autowired
    WalletRepo walletRepo;

    @Autowired
    WalletAuditRepo walletAuditRepo;


    /**
     * Top-ups the wallet balance for a given user.
     * <p>
     * This method performs the following:
     * 1. Fetches the user's wallet using the provided user ID from the `walletTopUpRequest`.
     * 2. If the wallet is not found, it returns a message stating "User's wallet not found" with a 404 NOT FOUND response.
     * 3. If the wallet is found, it adds the top-up amount to the current balance.
     * 4. Saves the updated wallet balance in the database.
     * 5. Logs the top-up transaction in the `WalletAudit` table, recording the transaction type (CREDIT), the top-up amount, and the new balance.
     * 6. Returns a success message with the top-up details and an HTTP 200 OK response.
     *
     * @param walletTopUpRequest The request containing user ID and top-up amount.
     * @return A `ResponseEntity` containing the success message, top-up data, and corresponding HTTP status.
     */
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

    /**
     * Fetches the wallet for a given user by user ID.
     *
     * This method performs the following:
     * 1. Retrieves the wallet for the user with the provided `id` from the `walletRepo`, ensuring that the wallet is not marked as deleted (`isDeleted` is false).
     * 2. If the wallet is not found, it returns a message stating "User's wallet not found" with a 404 NOT FOUND response.
     * 3. If the wallet is found, it returns the wallet data with a 200 OK response.
     *
     * @param id The user ID for which the wallet is being fetched.
     * @return A `ResponseEntity` containing the wallet data and the corresponding HTTP status.
     */
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

    /**
     * Deletes (marks as deleted) the wallet for a given user by user ID.
     *
     * This method performs the following:
     * 1. Retrieves the wallet for the user with the provided `userId` from the `walletRepo`, ensuring that the wallet is not already marked as deleted (`isDeleted` is false).
     * 2. If the wallet is not found, it returns a message stating "User's wallet not found" with a 404 NOT FOUND response.
     * 3. If the wallet is found, it sets the `isDeleted` flag to `true`, saves the wallet, and returns a success message stating "User Wallet deleted" with a 200 OK response.
     *
     * @param userId The user ID for which the wallet is being deleted.
     * @return A `ResponseEntity` containing the success or error message and the corresponding HTTP status.
     */
    public ResponseEntity<Object> DeleteWallet(int userId) {
        Map<String, Object> map = new HashMap<>();
        Wallet wallet = walletRepo.findByUserIdAndIsDeletedFalse(userId);
        if (wallet == null) {
            map.put("message", "User's wallet not found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            wallet.setDeleted(true);
            walletRepo.save(wallet);
            map.put("message", "User Wallet deleted");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> fetchWalletAudict(int id, HttpServletRequest request) {
        List<WalletAudit> usersAudict=walletAuditRepo.findByUserId(id);
        Map<String, Object> map = new HashMap<>();
        if(usersAudict.isEmpty())
        {
            map.put("message","No user wallet tranaction found");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else {
            map.put("message","user wallet tranaction are");
            map.put("data",usersAudict);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
    }


}
