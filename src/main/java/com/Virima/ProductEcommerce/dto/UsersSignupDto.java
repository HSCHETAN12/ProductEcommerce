package com.Virima.ProductEcommerce.dto;

import com.Virima.ProductEcommerce.Entity.Address;
import com.Virima.ProductEcommerce.Entity.Role;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class UsersSignupDto {
    int id;
    @Size(min = 3, max = 10, message = "It should be between 3 and 10 charecters")
    private String firstname;
    @Size(min = 1, max = 15, message = "It should be between 1 and 15 charecters")
    private String lastname;
    @Size(min = 5, max = 15, message = "It should be between 5 and 15 charecters")
    private String username;
    @Email(message = "It should be proper Email format")
    @NotEmpty(message = "It is required Field")
    private String email;
    @DecimalMin(value = "6000000000", message = "It should be proper mobile number")
    @DecimalMax(value = "9999999999", message = "It should be proper mobile number")
    private long mobile;
    @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
    private String password;
    @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
    private String confirmpassword;
    @NotNull(message = "It is required Field")
    private String gender;

    double balance;

    public UsersSignupDto(int id, String firstname, String lastname, String username, String email, String password, double balance, Long mobile, String gender) {
        this.id=id;
        this.firstname=firstname;
        this.lastname=lastname;
        this.username=username;
        this.email=email;
        this.password=password;
        this.mobile=mobile;
        this.balance=balance;
        this.gender=gender;
    }
}
