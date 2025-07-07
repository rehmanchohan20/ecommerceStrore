package com.ecomerce.sell.model.Vos;

import com.ecomerce.sell.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVo {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private int stockQuantity;
    private String imageStr;
    private Boolean isActive;


    public static ProductVo getAllProducts(Product product) {
        ProductVo productVo = new ProductVo();
        if (product != null) {
            productVo.setId(product.getId());
            productVo.setName(product.getName());
            productVo.setDescription(product.getDescription());
            productVo.setPrice(product.getPrice());
            productVo.setImageUrl(product.getImageUrl());
            productVo.setImageStr(product.getImageStr());
            productVo.setStockQuantity(product.getStockQuantity());
            productVo.setIsActive(product.getIsActive());

            System.out.println("Category: " + product.getCategory());

            if (product.getCategory() != null) {
                productVo.setCategoryId(product.getCategory().getId());
                productVo.setCategoryName(product.getCategory().getName());
            }
        }
        return productVo;
    }
}
