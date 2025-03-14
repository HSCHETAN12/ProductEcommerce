package com.Virima.ProductEcommerce.Service;

import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.dto.AddressDto;
import com.Virima.ProductEcommerce.dto.CartRequest;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UsersService {
     ResponseEntity<Object> save(@Valid UsersSignupDto user, BindingResult bindingResult);
     ResponseEntity<Object> userLogin(UserloginDto user, HttpSession session);
     ResponseEntity<Object> verify(int otp, int id);
     ResponseEntity<Object> addProductsToCart(HttpServletRequest request, List<CartRequest> cart) throws ProductException;
     ResponseEntity<Object> applyPromocode(String code, HttpServletRequest request);
     ResponseEntity<Object> checkouts(HttpServletRequest request) throws ProductException;
     ResponseEntity<Object> cancleorders(int orderId, HttpServletRequest request);
     ResponseEntity<Object> address(AddressDto addressRequest, HttpServletRequest request);
     ResponseEntity<Object> updateaddress(AddressDto address, HttpServletRequest request);

     ResponseEntity<Object> fetchuserorders(HttpServletRequest request);
}
