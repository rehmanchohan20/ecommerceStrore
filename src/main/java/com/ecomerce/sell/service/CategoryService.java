package com.ecomerce.sell.service;

import com.ecomerce.sell.model.Vos.CategoryVo;
import com.ecomerce.sell.util.response.Response;

public interface CategoryService {
    Response getAllCategories();
    Response addCategory(CategoryVo categoryVo);
    Response deleteCategory(Long id);
    Response getSubCategoriesByParentId(Long id);
}
