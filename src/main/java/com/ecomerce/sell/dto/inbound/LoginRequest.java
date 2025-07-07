package com.ecomerce.sell.dto.inbound;


import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

