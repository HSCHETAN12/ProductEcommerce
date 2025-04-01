//package com.Virima.ProductEcommerce.Controller;
//
//
//
//import com.Virima.ProductEcommerce.Service.AdminService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.web.bind.annotation.*;
//
//
//
//@RestController
////@RequestMapping("/admin")
//
//public class AdminController {
//
//    @Autowired
//    AdminService adminServiceImp;
//
////    @PostMapping("/adminlogin")
////    public ResponseEntity<Object> adminLogin(@RequestBody UserloginDto user) {
////        return adminServiceImp.verify(user);
////    }
//
//
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @PostMapping("/category")
////    public ResponseEntity<Object> addCategory(@RequestBody CategoryDto categoryDto)
////    {
////        return adminServiceImp.addCategory(categoryDto);
////    }
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @PostMapping("/products")
////    public ResponseEntity<Object> saveProduct(@RequestParam("name") String name,
////                                              @RequestParam("price") double price,
////                                              @RequestParam("stock") int stock,
////                                              @RequestParam("category") String category, @RequestParam("image") MultipartFile image) {
////        return adminServiceImp.product(name, price, stock, category, image);
////    }
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @GetMapping("/products")
////    public ResponseEntity<Object> fetchProducts()
////    {
////        return adminServiceImp.fetchProducts();
////    }
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @GetMapping("/products/{category}")
////    public ResponseEntity<Object> fetchByCategory(@PathVariable String category) {
////        return adminServiceImp.fetchByCategory(category);
////    }
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @GetMapping("/products/name/{name}")
////    public ResponseEntity<Object> fetchByName(@PathVariable String name) {
////        return adminServiceImp.fetchByName(name);
////    }
//
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @PostMapping("/wallettopup")
////    public ResponseEntity<Object> WalletTopUp(@RequestBody WalletTopUpDto walletTopUpRequest) {
////        return adminServiceImp.WalletTopUp(walletTopUpRequest);
////    }
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @PostMapping("/promocode")
////    public ResponseEntity<Object> createPromoCode(@RequestBody PromoCodeDto promoCodeDto) {
////
////        return adminServiceImp.createPromoCode(promoCodeDto);
////
////    }
//
//
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
////    @GetMapping("/orders")
////    public ResponseEntity<Object> fetchOrders(HttpServletRequest request) {
////        return adminServiceImp.fetchOrders(request);
////    }
//
//}
