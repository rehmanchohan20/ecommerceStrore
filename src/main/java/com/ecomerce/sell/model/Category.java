package com.ecomerce.sell.model;

import com.ecomerce.sell.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category extends BaseEntity {

    private String name;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
}

