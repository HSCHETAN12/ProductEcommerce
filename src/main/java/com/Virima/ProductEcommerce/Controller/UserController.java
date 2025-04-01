package com.Virima.ProductEcommerce.Controller;



import com.Virima.ProductEcommerce.Service.UsersService;
import com.Virima.ProductEcommerce.dto.UserloginDto;
import com.Virima.ProductEcommerce.dto.UsersSignupDto;
import com.Virima.ProductEcommerce.dto.UsersUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    UsersService userService;


    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody UsersSignupDto user, BindingResult bindingResult) {
        return userService.save(user, bindingResult);
    }

    @PostMapping("/verifiys/{id}")
    public ResponseEntity<Object> virify(@PathVariable int id, @RequestParam int otp)
    {
        return userService.verify(otp,id);
    }

    @PostMapping("/userslogin")
    public ResponseEntity<Object> userLogin(@RequestBody UserloginDto user,HttpSession session) {
        return userService.userLogin(user,session);
    }

    @PostMapping("/resend-otp/{id}")
    public ResponseEntity<Object> resendOtp(@PathVariable int id)
    {
        return userService.resendOtp(id);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/updateprofile")
    public ResponseEntity<Object> updateProfile(@RequestBody UsersUpdateDto user, HttpServletRequest request)
    {
        return userService.updateProfile(user,request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/userprofile")
    public ResponseEntity<Object> fetchProfile(HttpServletRequest request)
    {
        return userService.fetchProfile(request);
    }



    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Object> fetchUser() {
        return userService.fetchUser();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchUser(@PathVariable int id) {
        return userService.fetchUsers(id);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/fetchuserorders")
    public ResponseEntity<Object> fetchuserorders( HttpServletRequest request)
    {
        return userService.fetchuserorders(request);
    }
}
