package com.ecomerce.sell.mdoel;

import com.ecomerce.sell.mdoel.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class Users extends BaseEntity {

    private String username;
    private String password;
    private String email;
    private Boolean isActive;
    private String phoneNumber;
    private String address;
    private Boolean isAdmin;
    private Boolean isBuyer;

}
