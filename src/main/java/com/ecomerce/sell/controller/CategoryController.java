package com.ecomerce.sell.controller;

import com.ecomerce.sell.model.Vos.CategoryVo;
import com.ecomerce.sell.service.CategoryService;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<?> getAllCategories() {
        Response response = categoryService.getAllCategories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> addCategory(@RequestBody CategoryVo categoryVo) {
        Response response = categoryService.addCategory(categoryVo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteCategory(@Param("id") Long id) {
        Response response = categoryService.deleteCategory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //get sub categories by parent id
    @GetMapping("/subcategories")
    public ResponseEntity<?> getSubCategoriesByParentId(@Param("parentId") Long parentId) {
        Response response = categoryService.getSubCategoriesByParentId(parentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
