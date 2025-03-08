package com.Virima.ProductEcommerce.Security;

import com.Virima.ProductEcommerce.Exception.ProductException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
//            username = jwtService.extractUserName(token); // Extract username from the token
            username = jwtService.extractUsername(token);
            System.out.println(username);
        }
        String role = jwtService.extractRole(token); // Extract role


        if (role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Extract role from the token


            if (jwtService.validateToken(token, userDetails.getUsername())) {
                // Set the role in the Authentication token
                System.err.println(role);
//                Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority( role));
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singletonList(new SimpleGrantedAuthority(role))); // ✅ Use role from JWT
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

