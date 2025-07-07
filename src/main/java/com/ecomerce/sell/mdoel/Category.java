package com.ecomerce.sell.mdoel;

import com.ecomerce.sell.mdoel.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
}

