package com.Virima.ProductEcommerce.Service;


import com.Virima.ProductEcommerce.dto.UserloginDto;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;


public interface UsersService {
     ResponseEntity<Object> save(@Valid UsersSignupDto user, BindingResult bindingResult);
     ResponseEntity<Object> userLogin(UserloginDto user, HttpSession session);
     ResponseEntity<Object> verify(int otp, int id);

     ResponseEntity<Object> fetchuserorders(HttpServletRequest request);
     ResponseEntity<Object> fetchUser();
     ResponseEntity<Object> fetchUsers(int id);
}
