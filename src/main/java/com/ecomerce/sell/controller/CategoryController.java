package com.ecomerce.sell.controller;

import com.ecomerce.sell.service.CategoryService;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
