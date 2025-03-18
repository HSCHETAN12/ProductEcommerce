package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.*;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.CartRepo;
import com.Virima.ProductEcommerce.Repo.ProductRepo;
import com.Virima.ProductEcommerce.Repo.PromocodeRepo;
import com.Virima.ProductEcommerce.Service.PromoCodeService;
import com.Virima.ProductEcommerce.dto.PromoCodeDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PromoCodeServiceImp implements PromoCodeService {

    @Autowired
    PromocodeRepo promocodeRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    HelperMethods helperMethods;

    /**
     * Creates a new promo code and saves it to the database.
     * <p>
     * 1. Checks if a promo code with the same code and active status already exists in the database.
     * 2. If the promo code exists, returns a 200 (OK) response with a message indicating it already exists.
     * 3. If the promo code does not exist, creates a new PromoCode object using the provided data.
     * 4. If the promo code type is ORDER_BASED, sets the product name to null.
     * 5. Saves the new promo code to the database.
     * 6. Returns a 200 (OK) response with a success message and the saved promo code data.
     *
     * @param promoCodeDto The data transfer object containing promo code details (code, discount, type, dates, etc.).
     * @return A ResponseEntity with a message, data, and HTTP status code.
     */

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

    /**
     * Updates an existing promo code based on the provided data.
     * <p>
     * 1. Checks if a promo code with the specified code and active status exists.
     * 2. If the promo code is found, updates its fields based on the non-null values in the provided DTO.
     * 3. If the promo code type is ORDER_BASED, sets the product name to null.
     * 4. Saves the updated promo code to the database.
     * 5. Returns a 200 (OK) response with a success message and updated promo code data.
     * 6. If the promo code is not found or inactive, returns a 404 (NOT_FOUND) response with a corresponding message.
     *
     * @param code         The promo code to update.
     * @param promoCodeDto The data transfer object containing the updated promo code details.
     * @return A ResponseEntity with a message, updated data, and HTTP status code.
     */

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

    /**
     * Fetches a promo code by its code.
     * <p>
     * 1. Retrieves the promo code from the database using the provided code.
     * 2. If the promo code is not found, returns a 404 (NOT_FOUND) response with a message indicating no promo code found.
     * 3. If the promo code is found, returns a 200 (OK) response with the promo code data.
     *
     * @param code The promo code to fetch.
     * @return A ResponseEntity containing the promo code data (if found) and an appropriate message.
     */

    public ResponseEntity<Object> fetchPromoCode(String code) {
        Map<String, Object> map = new HashMap<>();
        PromoCode promoCode = promocodeRepo.findByCode(code);
        if (promoCode == null) {
            map.put("message", "No promocode found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            map.put("data", promoCode);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /**
     * Fetches all promo codes from the database.
     * <p>
     * 1. Retrieves a list of all promo codes from the database.
     * 2. If no promo codes are found, returns a 404 (NOT_FOUND) response with a message indicating no promo codes are available.
     * 3. If promo codes are found, returns a 200 (OK) response with the list of promo codes.
     *
     * @return A ResponseEntity containing the list of promo codes (if available) and an appropriate message.
     */

    public ResponseEntity<Object> fetchAllPromoCode() {
        Map<String, Object> map = new HashMap<>();
        List<PromoCode> promoCodes = promocodeRepo.findAll();
        if (promoCodes == null) {
            map.put("message", "No promocode found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            map.put("data", promoCodes);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /**
     * Applies a promo code to the user's active cart, calculating the discount based on the promo code type.
     * <p>
     * 1. Checks if the user is logged in; if not, returns a 404 (NOT_FOUND) response.
     * 2. Verifies if the provided promo code exists and is active; if not, returns a 404 (NOT_FOUND) response.
     * 3. Retrieves the active cart for the user; if no cart exists, returns a 404 (NOT_FOUND) response.
     * 4. Depending on the promo code type:
     * - For ORDER_BASED: applies the discount to the total amount of the cart.
     * - For PRODUCT_BASED: applies the discount to specific products in the cart matching the promo code’s product name.
     * 5. Updates the cart with the calculated discount and saves the updated cart.
     * 6. Returns a 200 (OK) response with a success message if the promo code is successfully applied.
     *
     * @param code    The promo code to apply.
     * @param request The HTTP request containing the logged-in user information.
     * @return A ResponseEntity with a success or error message and an appropriate HTTP status code.
     */
    @Transactional
    public ResponseEntity<Object> applyPromocode(String code, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Users user = helperMethods.role(request);
        if (user == null) {
            map.put("message", "User Not Login");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            Optional<PromoCode> promoCode = promocodeRepo.findByCodeAndStatus(code, PromoCodeStatus.ACTIVE);
            if (!promoCode.isPresent()) {
                map.put("message", "No promo code is present or inactive");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }

//            PromoCode promoCode = optionalPromoCode.get();
//
//            if (!promoCode.isValid()) {
//                map.put("message", "Promocode is inactive");
//                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//            }

            Cart cart = cartRepo.findByUserIdAndStatus(user.getId(), "active");
            if (cart == null) {
                map.put("message", "No cart found for the user " + user.getUsername());
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            } else {
                double totalDiscount = 0.0;

                if (promoCode.get().getType() == PromoCodeType.ORDER_BASED) {
                    // Apply the discount to the total amount of the cart
                    totalDiscount = promoCode.get().getDiscountValue();

                } else if (promoCode.get().getType() == PromoCodeType.PRODUCT_BASED) {
                    System.err.println("------------------------------------");
                    for (CartItem item : cart.getCartItems()) {
                        Optional<Products> productOpt = productRepo.findById(item.getProductId());
                        if (productOpt.isPresent()) {
                            Products product = productOpt.get();
                            if (product.getName().equals(promoCode.get().getProductName())) {
                                double discount = item.getQuantity() * product.getPrice() * promoCode.get().getDiscountValue() / 100;
                                totalDiscount += discount;
                                System.out.println(totalDiscount);
                            }
                        }
                    }
                }

                // Apply total discount to the cart
                cart.setTotalAmount(cart.getTotalAmount() - totalDiscount);
                PromoCode promoCode1 = promocodeRepo.findByCode(code);
                cart.setPromoCode(promoCode1);
                cartRepo.save(cart);

                map.put("message", "Promocode is applied successfully");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }
    }
}




