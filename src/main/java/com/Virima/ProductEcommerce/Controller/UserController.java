package com.Virima.ProductEcommerce.Controller;


import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.ServiceImplemantation.UsersServiceImp;
import com.Virima.ProductEcommerce.dto.AddressDto;
import com.Virima.ProductEcommerce.dto.CartRequest;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/users")
public class UserController {

    @Autowired
    UsersServiceImp userServiceImp;


    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody UsersSignupDto user, BindingResult bindingResult) {
        return userServiceImp.save(user, bindingResult);
    }

    @PostMapping("/verifiys/{id}")
    public ResponseEntity<Object> virify(@PathVariable int id, @RequestParam int otp)
    {
        return userServiceImp.verify(otp,id);
    }

    @PostMapping("/userslogin")
    public ResponseEntity<Object> userLogin(@RequestBody UserloginDto user,HttpSession session) {
        return userServiceImp.userLogin(user,session);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/cart/add")
    public ResponseEntity<Object> addCart(@RequestBody List<CartRequest> cart, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
//        Users user = (Users) session.getAttribute("user");
//        if (user == null) {
//            map.put("message", "User Not Login");
//            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//        }
        try {
            return userServiceImp.addProductsToCart(request, cart);
        } catch (ProductException e) {
            map.put("message", "Product Not Found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/promocode/{code}")
    public ResponseEntity<Object> applyPromocode(@PathVariable String code, HttpServletRequest request)
    {
        return userServiceImp.applyPromocode(code,request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/checkouts")
    public ResponseEntity<Object> checkouts(HttpServletRequest request) throws ProductException {
        Map<String, Object> map = new HashMap<>();
        return userServiceImp.checkouts(request);
    }
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/cancleorders/{orderId}")
    public ResponseEntity<Object> cancleorders(@PathVariable int orderId, HttpServletRequest request)  {
        return userServiceImp.cancleorders(orderId,request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/address")
    public ResponseEntity<Object> address(@RequestBody AddressDto address, HttpServletRequest request)
    {
        return userServiceImp.address(address,request);
    }
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/updateaddress")
    public ResponseEntity<Object> updateaddress(@RequestBody AddressDto address, HttpServletRequest request)
    {
        return userServiceImp.updateaddress(address,request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/fetchuserorders")
    public ResponseEntity<Object> fetchuserorders( HttpServletRequest request)
    {
        return userServiceImp.fetchuserorders(request);
    }
}
