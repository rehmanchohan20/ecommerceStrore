package com.ecomerce.sell.mdoel.Vos;

import com.ecomerce.sell.mdoel.Users;
import lombok.Data;

@Data
public class UsersVo {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean active;
    private Boolean isBuyer;
    private String phoneNumber;
    private String address;

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
        }
        return usersVo;
    }
}
