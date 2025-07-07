package com.ecomerce.sell.model.Vos;

import com.ecomerce.sell.model.Users;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsersVo {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean active;
    private Boolean isBuyer;
    private String phoneNumber;
    private String address;
    private List<String> roles; // ✅ NEW

    public static UsersVo getAllUsers(Users user){
        UsersVo usersVo = new UsersVo();
        if(user != null){
            usersVo.setId(user.getId());
            usersVo.setUsername(user.getUsername());
            usersVo.setEmail(user.getEmail());
            usersVo.setPassword(user.getPassword());
            usersVo.setActive(user.getIsActive());
            usersVo.setIsBuyer(user.getIsBuyer());
            usersVo.setPhoneNumber(user.getPhoneNumber());
            usersVo.setAddress(user.getAddress());

            // ✅ Set role names
            if (user.getRoles() != null) {
                usersVo.setRoles(
                        user.getRoles().stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toList())
                );
            }
        }
        return usersVo;
    }
}
