package com.Virima.ProductEcommerce.dto;

import lombok.Data;

@Data
public class UsersUpdateDto {

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private long mobile;
    private String password;
    private String gender;
    private double balance;

    public UsersUpdateDto(String firstname, String lastname, String username, String email,
                          String password,  double balance, long mobile, String gender) {

        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;

        this.balance = balance;
        this.mobile = mobile;
        this.gender = gender;
    }
}
