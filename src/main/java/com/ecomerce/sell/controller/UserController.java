package com.ecomerce.sell.controller;

import com.ecomerce.sell.mdoel.Users;
import com.ecomerce.sell.mdoel.Vos.UsersVo;
import com.ecomerce.sell.service.serviceImpl.UserServiceImpl;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping()
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "10") int pageSize,@RequestParam(defaultValue = "1") int pageNumber) {
        Response response = userService.getAllUsers(pageSize, pageNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<?> addUser(@RequestBody UsersVo user) {
        Response response = userService.saveUser(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        Response response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }



}
