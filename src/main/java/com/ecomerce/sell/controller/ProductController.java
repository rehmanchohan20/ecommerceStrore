package com.ecomerce.sell.controller;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Product;
import com.ecomerce.sell.model.Vos.ProductVo;
import com.ecomerce.sell.service.ProductService;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

     @GetMapping("/getAll")
     public ResponseEntity<?> getAllProducts(@RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber){
         Response response = productService.getAllProducts(pageSize, pageNumber);
         return ResponseEntity.ok(response);
     }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Response response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductVo product) {
        Response response = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

     @PostMapping("/delete")
     public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
        Response response = productService.deleteProduct(productId);
        return ResponseEntity.ok(response);
     }
}
