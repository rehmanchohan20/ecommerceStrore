package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.mdoel.Users;
import com.ecomerce.sell.mdoel.Vos.UsersVo;
import com.ecomerce.sell.repository.UserRepository;
import com.ecomerce.sell.service.UserService;
import com.ecomerce.sell.util.response.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Response getAllUsers(int pageSize, int pageNumber) {
        Response response = new Response();

        try{
            Pageable pageable = PageRequest.of(pageNumber -1, pageSize, Sort.by("id").descending());
            List<Users> all = userRepository.findAll(pageable).getContent();
           if(!all.isEmpty()){
               List<UsersVo> usersVoList = new ArrayList<>();
               for(Users user : all){
                   UsersVo usersVo = UsersVo.getAllUsers(user);
                   usersVoList.add(usersVo);
               }
               response.setResponse(DAOResponse.SUCCESS);
               response.setData("users", usersVoList);
           }else{
                response.setResponse(DAOResponse.NO_DATA_FOUND);
           }

        }catch (Exception e){
            e.printStackTrace();
            logger.severe("Error fetching users: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response saveUser(UsersVo userVo) {
        Response response = new Response();
        Users user = new Users();

        try {
            if (userVo == null) {
                response.setResponse(DAOResponse.INVALID_REQUEST);
                return response;
            }

            if (userVo.getId() == null) {
                Users existingByEmail = userRepository.findByEmail(userVo.getEmail());
                if (existingByEmail != null) {
                    response.setCode("400");
                    response.setMessage("User with email " + userVo.getEmail() + " already exists");
                    return response;
                }

                Users existingByUsername = userRepository.findByUsername(userVo.getUsername());
                if (existingByUsername != null) {
                    response.setCode("400");
                    response.setMessage("User with username " + userVo.getUsername() + " already exists");
                    return response;
                }

                user.setUsername(userVo.getUsername());
                user.setPassword(userVo.getPassword());
                user.setEmail(userVo.getEmail());
                user.setIsActive(userVo.getActive());
                user.setPhoneNumber(userVo.getPhoneNumber());
                user.setAddress(userVo.getAddress());
                user.setIsAdmin(false);
                user.setIsBuyer(userVo.getIsBuyer() != null ? userVo.getIsBuyer() : false);
                user.setCreatedBy("self");

                Users savedUser = userRepository.save(user);
                UsersVo usersVo = UsersVo.getAllUsers(savedUser);
                response.setResponse(DAOResponse.SUCCESS);
                response.setData("user", usersVo);
                return response;
            }

            else {
                Users existingUser = userRepository.findById(userVo.getId()).orElse(null);
                if (existingUser != null) {
                    existingUser.setUsername(userVo.getUsername());
                    existingUser.setPassword(userVo.getPassword());
                    existingUser.setEmail(userVo.getEmail());
                    existingUser.setUpdatedBy("admin");
                    existingUser.setPhoneNumber(userVo.getPhoneNumber());
                    existingUser.setAddress(userVo.getAddress());
                    existingUser.setIsBuyer(userVo.getIsBuyer());
                    existingUser.setIsActive(userVo.getActive());

                    Users updatedUser = userRepository.save(existingUser);
                    UsersVo usersVo = UsersVo.getAllUsers(updatedUser);
                    response.setResponse(DAOResponse.SUCCESS);
                    response.setData("user", usersVo);
                } else {
                    response.setResponse(DAOResponse.USER_NOT_FOUND);
                }
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error saving user: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }
        return response;
    }

    @Override
    public  Response deleteUser(Long userId) {
        Response response = new Response();
        try {
            Users user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setIsActive(user.getIsActive());
                userRepository.delete(user);
                response.setResponse(DAOResponse.SUCCESS);
            } else {
                response.setResponse(DAOResponse.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error deleting user: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }
        return response;
    }


}
