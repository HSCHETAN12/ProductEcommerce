package com.Virima.ProductEcommerce.ServiceImplemantation;

import com.Virima.ProductEcommerce.Entity.Address;
import com.Virima.ProductEcommerce.Entity.Users;
import com.Virima.ProductEcommerce.Exception.ProductException;
import com.Virima.ProductEcommerce.Helper.HelperMethods;
import com.Virima.ProductEcommerce.Repo.AddressRepo;
//import com.Virima.ProductEcommerce.Service.AddressService;
import com.Virima.ProductEcommerce.Service.AddressService;
import com.Virima.ProductEcommerce.dto.AddressDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddressServiceImp implements AddressService {

    @Autowired
    HelperMethods helperMethods;

    @Autowired
   AddressRepo addressRepo;
    /**
     * Adds a new address for the logged-in user if the user has the "USER" role.
     *
     * This method performs the following:
     * 1. Retrieves the logged-in user by checking the request's authentication details.
     * 2. Verifies if the logged-in user has the "USER" role. If not, it returns a "FORBIDDEN" status.
     * 3. Creates a new `Address` object and populates it with the address details provided in the `addressRequest` DTO.
     * 4. Associates the new address with the logged-in user.
     * 5. Saves the address to the database using `addressRepo.save(address)`.
     * 6. Returns a success message along with the address data in the response.
     *
     * @param addressRequest The address data provided by the user in the request.
     * @param request The HTTP request containing user authentication details.
     * @return A `ResponseEntity` containing the result of the address addition operation and the corresponding HTTP status.
     */
    public ResponseEntity<Object> address(AddressDto addressRequest, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            Users user = helperMethods.role(request);
            System.out.println("-------------------------------");
            System.out.println(user.getRole().getName());
            if (user.getRole().getName().equals("USER")) {
                Address address = new Address();
                address.setStreet(addressRequest.getStreet());
                address.setCity(addressRequest.getCity());
                address.setState(addressRequest.getState());
                address.setCountry(addressRequest.getCountry());
                address.setPostalCode(addressRequest.getPostalCode());
                address.setUser(user); // Associate address with user
                addressRepo.save(address);
                map.put("message", "Address added successfull");
                map.put("data", addressRequest);
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the address for the logged-in user.
     *
     * This method performs the following:
     * 1. Retrieves the logged-in user using the helper method and checks if the user has the "USER" role. If the role is not "USER", it returns a "FORBIDDEN" status.
     * 2. Fetches the existing address associated with the logged-in user.
     * 3. If the user doesn't have an address, it returns a "NOT_FOUND" response.
     * 4. Updates the address fields (street, city, state, country, postal code) only if the corresponding fields in the request are provided.
     * 5. Saves the updated address in the database.
     * 6. Returns a success message along with the updated address in the response.
     *
     * @param address The address data to update, provided by the user in the request.
     * @param request The HTTP request containing user authentication details.
     * @return A `ResponseEntity` containing the result of the address update operation and the corresponding HTTP status.
     */

    public ResponseEntity<Object> updateaddress(Long id,AddressDto address, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {

            Users user = helperMethods.role(request);
            System.out.println(user.getAddresses());
            if (!user.getRole().getName().equals("USER")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // Fetch the user's current address (assuming user.getAddress() gets the address object)
            Address existingAddress = addressRepo.findById(id).get();
            System.out.println(existingAddress);
            // If the user doesn't have an address, return a 404 not found response
            if (existingAddress == null) {
//                map.put("message", "Address not found");
//                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
                throw new ProductException("Address Not Found");
            }

            // Update the fields of the address (only if they are provided)
            if (address.getStreet() != null) {
                existingAddress.setStreet(address.getStreet());
            }
            if (address.getCity() != null) {
                existingAddress.setCity(address.getCity());
            }
            if (address.getState() != null) {
                existingAddress.setState(address.getState());
            }
            if (address.getCountry() != null) {
                existingAddress.setCountry(address.getCountry());
            }
            if (address.getPostalCode() != null) {
                existingAddress.setPostalCode(address.getPostalCode());
            }

            // Save the updated address
            addressRepo.save(existingAddress);

            // Prepare the response message
            map.put("message", "Address updated successfully");
            map.put("data", existingAddress);
            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (Exception e) {
            map.put("message", "An error occurred while updating the address");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> fetchAddress(HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Users user = helperMethods.role(request);
            List<Address> addressList = addressRepo.findByUserId(user.getId());

            if (addressList == null || addressList.isEmpty()) {
                throw new ProductException("No address present. Please add an address.");
            }

            // Convert Address entity list to AddressDto list
            List<AddressDto> addressDtoList = addressList.stream().map(address -> {
                AddressDto dto = new AddressDto();
                dto.setStreet(address.getStreet());
                dto.setCity(address.getCity());
                dto.setId(address.getId());
                dto.setState(address.getState());
                dto.setCountry(address.getCountry());
                dto.setPostalCode(address.getPostalCode());
                return dto;
            }).collect(Collectors.toList());

            responseMap.put("data", addressDtoList);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (ProductException e) {
            responseMap.put("error", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            responseMap.put("error", "An unexpected error occurred.");
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteAddress(Long addressId,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Get the current user from the request
            Users user = helperMethods.role(request);

            // Find the address by its ID and check if it exists
            Address address = addressRepo.findById(addressId).orElseThrow(() -> new ProductException("Address not found"));

            // Ensure the address belongs to the current user
            if (address.getUser().getId() != user.getId()) {
                throw new ProductException("Address does not belong to the current user");
            }

            // Delete the address
            addressRepo.delete(address);

            map.put("message", "Address deleted successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (ProductException e) {
            map.put("message", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            map.put("message", "An error occurred: " + e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
