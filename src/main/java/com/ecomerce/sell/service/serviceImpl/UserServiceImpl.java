package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Role;
import com.ecomerce.sell.model.Users;
import com.ecomerce.sell.model.Vos.UsersVo;
import com.ecomerce.sell.repository.RoleRepository;
import com.ecomerce.sell.repository.UserRepository;
import com.ecomerce.sell.service.UserService;
import com.ecomerce.sell.util.AuthUtils;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        Long userId = AuthUtils.getCurrentUserId();

        try {
            if (userVo == null) {
                response.setResponse(DAOResponse.INVALID_REQUEST);
                return response;
            }

            // Create new user
            if (userVo.getId() == null) {
                // Email and username validation
                if (userRepository.findByEmail(userVo.getEmail()) != null) {
                    response.setCode("400");
                    response.setMessage("User with email " + userVo.getEmail() + " already exists");
                    return response;
                }

                if (userRepository.findByUsername(userVo.getUsername()) != null) {
                    response.setCode("400");
                    response.setMessage("User with username " + userVo.getUsername() + " already exists");
                    return response;
                }

                // Basic fields
                user.setUsername(userVo.getUsername());
                user.setPassword(passwordEncoder.encode(userVo.getPassword()));
                user.setEmail(userVo.getEmail());
                user.setIsActive(userVo.getActive());
                user.setPhoneNumber(userVo.getPhoneNumber());
                user.setAddress(userVo.getAddress());
                user.setIsAdmin(false);
                user.setIsBuyer(userVo.getIsBuyer() != null ? userVo.getIsBuyer() : false);
                user.setCreatedBy(String.valueOf(userId));

                // 🔁 Set roles
                if (userVo.getRoles() != null && !userVo.getRoles().isEmpty()) {
                    Set<Role> roles = new HashSet<>();
                    for (String roleName : userVo.getRoles()) {
                        Role role = roleRepository.findByName(roleName);
                        if (role != null) {
                            roles.add(role);
                        }
                    }
                    user.setRoles(roles);
                }

                Users savedUser = userRepository.save(user);
                UsersVo usersVo = UsersVo.getAllUsers(savedUser);
                response.setResponse(DAOResponse.SUCCESS);
                response.setData("user", usersVo);
                return response;
            }

            // Update existing user
            else {
                Users existingUser = userRepository.findById(userVo.getId()).orElse(null);
                if (existingUser != null) {
                    existingUser.setUsername(userVo.getUsername());
                    existingUser.setPassword(passwordEncoder.encode(userVo.getPassword()));
                    existingUser.setEmail(userVo.getEmail());
                    existingUser.setUpdatedBy(String.valueOf(userId));
                    existingUser.setPhoneNumber(userVo.getPhoneNumber());
                    existingUser.setAddress(userVo.getAddress());
                    existingUser.setIsBuyer(userVo.getIsBuyer());
                    existingUser.setIsActive(userVo.getActive());

                    // 🔁 Update roles
                    if (userVo.getRoles() != null && !userVo.getRoles().isEmpty()) {
                        Set<Role> roles = new HashSet<>();
                        for (String roleName : userVo.getRoles()) {
                            Role role = roleRepository.findByName(roleName);
                            if (role != null) {
                                roles.add(role);
                            }
                        }
                        existingUser.setRoles(roles);
                    }

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
                user.setIsActive(false);
                user.setUpdatedBy(String.valueOf(userId));
                userRepository.save(user);
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
