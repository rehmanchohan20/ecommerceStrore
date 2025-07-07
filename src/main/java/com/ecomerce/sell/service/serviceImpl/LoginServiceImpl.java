package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.dto.inbound.LoginRequest;
import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Role;
import com.ecomerce.sell.model.Users;
import com.ecomerce.sell.repository.UserRepository;
import com.ecomerce.sell.service.LoginService;
import com.ecomerce.sell.util.JwtUtil;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());


    public Response login(LoginRequest loginRequest) {
        Response response = new Response();
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            Users user = userRepository.findByUsername(loginRequest.getUsername());

            // For simplicity, assuming one role per user
            String role = user.getRoles().stream()
                    .findFirst()
                    .map(Role::getName)
                    .orElse("ROLE_USER");

            String token = jwtUtil.generateToken(user.getUsername(), user.getId().toString(), role);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("userName", user.getUsername());
            responseData.put("role", role);
            responseData.put("userId", user.getId());
            response.setResponse(DAOResponse.SUCCESS);
            response.setResponseData(responseData);
        }catch (Exception e) {
            logger.severe("Login failed: " + e.getMessage());
            response.setResponse(DAOResponse.INVALID_CREDENTIALS);
            return response;
        }
        return response;
    }

}
