package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Category;
import com.ecomerce.sell.model.Vos.CategoryVo;
import com.ecomerce.sell.repository.CategoryRepository;
import com.ecomerce.sell.service.CategoryService;
import com.ecomerce.sell.util.AuthUtils;
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
    @Override
    public Response addCategory(CategoryVo categoryVo) {
        Response response = new Response();
        Long userId = AuthUtils.getCurrentUserId();

        try {
            if (categoryVo == null || categoryVo.getName() == null || categoryVo.getName().trim().isEmpty()) {
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("Category name cannot be empty");
                return response;
            }

            String trimmedName = categoryVo.getName().trim();
            Category parent = null;

            if (categoryVo.getParentId() != null) {
                parent = categoryRepository.findCategoriesById(categoryVo.getParentId());
                if (parent == null) {
                    response.setResponse(DAOResponse.NO_DATA_FOUND);
                    response.setMessage("Parent category not found");
                    return response;
                }
            }

            if (categoryVo.getId() == null) {
                // ADD case
                if (parent != null) {
                    if (categoryRepository.existsByNameIgnoreCaseAndParent(trimmedName, parent)) {
                        response.setCode("400");
                        response.setMessage("Sub-category with this name already exists under the selected parent category");
                        return response;
                    }
                } else {
                    if (categoryRepository.existsByNameIgnoreCaseAndParentIsNull(trimmedName)) {
                        response.setCode("400");
                        response.setMessage("Top-level category with this name already exists");
                        return response;
                    }
                }

                Category category = new Category();
                category.setName(trimmedName);
                category.setParent(parent);
                category.setCreatedBy(String.valueOf(userId));
                category.setUpdatedBy(String.valueOf(userId));
                category.setActive(true);
                categoryRepository.save(category);

            } else {
                // UPDATE case
                Category category = categoryRepository.findCategoriesById(categoryVo.getId());
                if (category == null) {
                    response.setResponse(DAOResponse.NO_DATA_FOUND);
                    response.setMessage("Category not found");
                    return response;
                }

                boolean nameChanged = !category.getName().equalsIgnoreCase(trimmedName);
                boolean parentChanged = (category.getParent() == null && parent != null)
                        || (category.getParent() != null && !category.getParent().getId().equals(categoryVo.getParentId()));

                if (nameChanged || parentChanged) {
                    if (parent != null) {
                        if (categoryRepository.existsByNameIgnoreCaseAndParent(trimmedName, parent)) {
                            response.setCode("400");
                            response.setMessage("Sub-category with this name already exists under the selected parent category");
                            return response;
                        }
                    } else {
                        if (categoryRepository.existsByNameIgnoreCaseAndParentIsNull(trimmedName)) {
                            response.setCode("400");
                            response.setMessage("Top-level category with this name already exists");
                            return response;
                        }
                    }
                }

                category.setName(trimmedName);
                category.setParent(parent);
                category.setUpdatedBy(String.valueOf(userId));
                categoryRepository.save(category);
            }

            response.setResponse(DAOResponse.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error adding/updating category: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }

        return response;
    }

    @Override
    public Response deleteCategory(Long id){
        Response response = new Response();
        try{
            if (id == null) {
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("Category ID cannot be null");
                return response;
            }

            Category category = categoryRepository.findCategoriesByIdAndActiveTrue(id);
            if (category == null) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("Category not found");
                return response;
            }

            category.setActive(false);

            List<Category> subCategories = categoryRepository.findByParentAndActiveTrue(category);
            for (Category subCategory : subCategories) {
                subCategory.setActive(false);
            }
            // Soft delete the category
            categoryRepository.save(category);              // Save parent
            categoryRepository.saveAll(subCategories);    // Save subcategories

            response.setResponse(DAOResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error deleting category: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
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
