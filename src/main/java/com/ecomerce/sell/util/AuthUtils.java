package com.ecomerce.sell.util;

import com.ecomerce.sell.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuthUtils {

    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        return null;
    }
}

