package com.ecomerce.sell.model.Vos;

import com.ecomerce.sell.model.Category;
import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Long id;
    private String name;
    private Long parentId;
    private List<CategoryVo> subCategories;


    public static CategoryVo fromCategory(Category category) {
        CategoryVo vo = new CategoryVo();
        vo.setId(category.getId());
        vo.setName(category.getName());

        return vo;
    }

}
