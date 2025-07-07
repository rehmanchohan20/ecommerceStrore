package com.ecomerce.sell.controller;

import com.ecomerce.sell.dto.inbound.LoginRequest;
import com.ecomerce.sell.service.LoginService;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Response loginResponse = loginService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
