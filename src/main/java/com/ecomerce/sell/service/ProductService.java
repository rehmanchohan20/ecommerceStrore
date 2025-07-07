package com.ecomerce.sell.service;

import com.ecomerce.sell.model.Product;
import com.ecomerce.sell.model.Vos.ProductVo;
import com.ecomerce.sell.util.response.Response;

public interface ProductService {
    Response getAllProducts(int pageSize, int pageNumber);
    Response createProduct(ProductVo productVo);
    Response deleteProduct(Long productId);
    Response getProductById(Long productId);
}
