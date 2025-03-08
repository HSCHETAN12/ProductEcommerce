package com.Virima.ProductEcommerce.Security;

import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users=userRepo.findByusername(username);
        if(users==null)
        {
            System.out.println("user Not Found");
            throw new UsernameNotFoundException("user not found");
        }
        return new UserPrincipal(users);
    }
}
