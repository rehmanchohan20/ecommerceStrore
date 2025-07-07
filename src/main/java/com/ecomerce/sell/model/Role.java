package com.ecomerce.sell.model;

import com.ecomerce.sell.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role extends BaseEntity {
    private String name;
}
