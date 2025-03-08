package com.Virima.ProductEcommerce.Controller;


import com.Virima.ProductEcommerce.ServiceImplemantation.UsersServiceImp;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UsersServiceImp userServiceImp;


    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody UsersSignupDto user, BindingResult bindingResult) {
        return userServiceImp.save(user, bindingResult);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/verifiys/{id}")
    public ResponseEntity<Object> virify(@PathVariable int id, @RequestParam int otp)
    {
        return userServiceImp.verify(otp,id);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> userLogin(@RequestBody UserloginDto user) {
        return userServiceImp.userLogin(user);
    }

}
