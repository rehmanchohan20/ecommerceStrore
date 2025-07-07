package com.ecomerce.sell.service;

import com.ecomerce.sell.dto.inbound.LoginRequest;
import com.ecomerce.sell.util.response.Response;

public interface LoginService {

    Response login(LoginRequest loginRequest);
}
