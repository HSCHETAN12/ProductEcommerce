package com.Virima.ProductEcommerce.Security;

import com.Virima.ProductEcommerce.Entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;
//    private String secretKey ;

//    public JWTService(){
//        try {
//            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey sk = keyGenerator.generateKey();
//            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
       String role1="ROLE_"+role;
        claims.put("role", role1); // Add the role as a claim
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000 * 10)) // Set expiration time
//                .signWith(getKey())
//                .compact();

        Instant now = Instant.now(); // Current timeInstant expiry = now.plusSeconds(3600);
        Instant expiry = now.plusSeconds(3600);
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(username)
                .claim("role", role1)
              .issuedAt(Date.from(now))
              .expiration(Date.from(expiry))
               .signWith(key);
               return jwtBuilder.compact();
    }

//    public SecretKey getKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

//    public String extractUserName(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser() // Use parserBuilder for newer versions
//                .setSigningKey(getKey()) // Set the signing key
//                .build() // Build the parser
//                .parseClaimsJws(token) // Parse the token
//                .getBody(); // Extract the claims body
//    }
//
//
//    public String extractRole(String token) {
//        return extractClaim(token, claims -> claims.get("role", String.class)); // Extract the role from the token
//    }
//
//    public boolean validateToken(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }

//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extract claims from the token
//    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimResolver.apply(claims);
//    }
//
////     Extract all claims from the token
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser() // Use parserBuilder() for better flexibility and support
//                .setSigningKey(getSigningKey()) // Use the correct signing key
//                .build() // Build the parser
//                .parseClaimsJws(token) // Parse the claims
//                .getBody(); // Extract the claims body
//    }
//    private Key getSigningKey() {
//        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));  // Use a byte array for the key
//    }
//
//    // Extract the role from the token
//    public String extractRole(String token) {
//        return extractClaim(token, claims -> claims.get("role", String.class));
//    }
//
//    // Validate if the token is valid
//    public boolean validateToken(String token, String username) {
//        return extractUsername(token).equals(username) && !isTokenExpired(token);
//    }
//
//    // Check if the token has expired
//    private boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }
//
//    // Extract all claims from the token, handling errors gracefully
//    public Claims extractClaims(String token) {
//        try {
//            return Jwts.parser() // Use parserBuilder for newer versions of JJWT
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token) // Parse the claims
//                    .getBody();
//        } catch (ExpiredJwtException e) {
//            System.out.println("Token expired: " + e.getMessage());
//        } catch (MalformedJwtException e) {
//            System.out.println("Malformed token: " + e.getMessage());
//        } catch (SignatureException e) {
//            System.out.println("Invalid signature: " + e.getMessage());
//        } catch (Exception e) {
//            System.out.println("Error parsing JWT: " + e.getMessage());
//        }
//        return null;  // Return null if an exception occurred
//    }

public Claims extractClaims(String token) {
    try {
        System.out.println("token: " + token);

        // Ensure the secret key is at least 32 bytes long
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 characters long!");
        }

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        JwtParser jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
        System.out.println("jwtParser: "+ jwtParser);

        return jwtParser.parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
        System.out.println("Error: Token is expired - " + e.getMessage());
        return null;
    } catch (MalformedJwtException e) {
        System.out.println("Error: Malformed token - " + e.getMessage());
        return null;
    } catch (SecurityException e) {
        System.out.println("Error: Invalid signature - " + e.getMessage());
        return null;
    } catch (Exception e) {
        System.out.println("Error parsing JWT: " + e.getMessage());
        return null;
    }
}

    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        if (claims == null) {
            return "claims are null, check extract claims";  // Return null instead of throwing NullPointerException
        }
        return claims.getSubject();
    }

    // Extract Role
    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    // Validate Token
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // Check Token Expiry
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

}

