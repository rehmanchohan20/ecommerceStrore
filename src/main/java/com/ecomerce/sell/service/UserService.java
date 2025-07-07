package com.ecomerce.sell.service;

import com.ecomerce.sell.model.Vos.UsersVo;
import com.ecomerce.sell.util.response.Response;

public interface UserService {
    Response getAllUsers(int pageSize, int pageNumber);

    Response saveUser(UsersVo userVo);

    Response deleteUser(Long userId);
}
