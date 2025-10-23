//package com.project.ecommerce.restcontroller;
//
//import com.ecommerce.appuser.address.UserAddressDTO;
//import com.ecommerce.appuser.address.UserAddressService;
//import com.ecommerce.exceptionhandler.APIResponse;
//import com.ecommerce.util.AppUtil;
//import com.ecommerce.util.PageDetails;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserController
//{
//
//    private final UserAddressService userAddressService;
//
//    public UserController(UserAddressService userAddressService)
//    {
//        this.userAddressService = userAddressService;
//    }
//
//    //Add user
//    //Remove user
//    //Update user
//
//    @PostMapping("/address/add/{userId}")
//    public ResponseEntity<APIResponse<UserAddressDTO>> addAddress(@PathVariable long userId, @RequestBody UserAddressDTO addressDTO)
//    {
//        UserAddressDTO addedAddress = userAddressService.addAddress(userId, addressDTO);
//
//        return new ResponseEntity<>(
//                new APIResponse<>("Address for user added!", true, addedAddress),
//                HttpStatus.OK
//        );
//    }
//
//    @PutMapping("/address/update/{userId}")
//    public ResponseEntity<APIResponse<UserAddressDTO>> updateAddress(@PathVariable long userId, @RequestBody UserAddressDTO addressDTO)
//    {
//        UserAddressDTO updatedAddress = userAddressService.updateAddress(userId, addressDTO);
//
//        return new ResponseEntity<>(
//                new APIResponse<>("User address updated!", true, updatedAddress),
//                HttpStatus.OK
//        );
//    }
//
//    @DeleteMapping("/address/delete/{userId}/{addressId}")
//    public ResponseEntity<APIResponse<Void>> deleteAddress(@PathVariable long userId, @PathVariable long addressId)
//    {
//        userAddressService.deleteAddress(userId, addressId);
//
//        return new ResponseEntity<>(
//                new APIResponse<>("User address deleted!", true, null),
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/fetch/specificAddress/forUser/{userId}/{addressId}")
//    public ResponseEntity<UserAddressDTO> fetchSpecificAddressForUser(@PathVariable long userId, @PathVariable long addressId) {
//        UserAddressDTO specificAddressForUser = userAddressService.fetchSpecificAddressForUser(userId, addressId);
//
//        return new ResponseEntity<>(
//                specificAddressForUser,
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/fetch/allAddresses/forUser/{userId}")
//    public ResponseEntity<PageDetails<UserAddressDTO>> fetchAllAddressesForUser(
//            @PathVariable long userId,
//            @RequestParam(name = "pageNum", defaultValue = AppUtil.DEFAULT_PAGE_NUMBER) int pageNum,
//            @RequestParam(value = "pageSize", defaultValue = AppUtil.DEFAULT_PAGE_SIZE) int pageSize,
//            @RequestParam(value = "sortBy", defaultValue = AppUtil.SORT_BY) String sortBy,
//            @RequestParam(value = "sortOrder", defaultValue = AppUtil.SORT_DIR) String sortOrder)
//    {
//        PageDetails<UserAddressDTO> fetchedAllAddressesForUser = userAddressService.fetchAllAddressesForUser(userId, pageNum, pageSize, sortBy, sortOrder);
//
//        return new ResponseEntity<>(
//                fetchedAllAddressesForUser,
//                HttpStatus.OK
//        );
//    }
//}
