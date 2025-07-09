package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Category;
import com.ecomerce.sell.model.Vos.CategoryVo;
import com.ecomerce.sell.repository.CategoryRepository;
import com.ecomerce.sell.service.CategoryService;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = Logger.getLogger(CategoryServiceImpl.class.getName());

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Response getAllCategories() {
        Response response = new Response();
        try {
            List<Category> topCategories = categoryRepository.findByParentIsNullAndActiveTrue();
            List<CategoryVo> result = new ArrayList<>();

            for (Category top : topCategories) {
                result.add(buildCategoryTree(top));
            }

            response.setResponse(DAOResponse.SUCCESS);
            response.setData("categories", result);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error fetching categories: " + e.getMessage());
        }
        return response;
    }




    private CategoryVo buildCategoryTree(Category category) {
        CategoryVo vo = CategoryVo.fromCategory(category);
        List<Category> children = categoryRepository.findByParentAndActiveTrue(category);

        if (!children.isEmpty()) {
            List<CategoryVo> childVos = new ArrayList<>();
            for (Category child : children) {
                childVos.add(buildCategoryTree(child));
            }
            vo.setSubCategories(childVos);
        }
        return vo;
    }






}
