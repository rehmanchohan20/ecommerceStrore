package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.Category;
import com.ecomerce.sell.model.Product;
import com.ecomerce.sell.model.Vos.ProductVo;
import com.ecomerce.sell.repository.CategoryRepository;
import com.ecomerce.sell.repository.ProductRepository;
import com.ecomerce.sell.service.ProductService;
import com.ecomerce.sell.util.AuthUtils;
import com.ecomerce.sell.util.ImageUtil;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger log = Logger.getLogger(ProductServiceImpl.class.getName());

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Response getAllProducts(int pageSize, int pageNumber) {
        Response response = new Response();
        try{
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
            List<Product> products = productRepository.findAll(pageable).getContent();
            List<ProductVo> productVos = new ArrayList<>();
            if(!products.isEmpty()){
                for(Product product : products){
                    ProductVo productVo = ProductVo.getAllProducts(product);
                    productVos.add(productVo);
                }
                response.setResponse(DAOResponse.SUCCESS);
                response.setData("products", productVos);
            } else {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.severe("error occurred while processing the request"+e.getMessage());
        }

        return response;
    }

    @Override
    public Response createProduct(ProductVo productVo) {
        Response response = new Response();
        Long userId = AuthUtils.getCurrentUserId();
        if (productVo == null) {
            response.setResponse(DAOResponse.INVALID_REQUEST);
            return response;
        }

        try {
            Product product;

            if (productVo.getId() != null) {
                product = productRepository.getProductById(productVo.getId());

                if (product == null) {
                    response.setResponse(DAOResponse.NO_DATA_FOUND);
                    return response;
                }

                // Update fields
                product.setName(productVo.getName());
                product.setDescription(productVo.getDescription());
                product.setPrice(productVo.getPrice());
                product.setStockQuantity(productVo.getStockQuantity());
                product.setIsActive(productVo.getIsActive());
                product.setUpdatedBy(String.valueOf(userId));

                if (productVo.getCategoryId() != null) {

                    Category category = new Category();
                    category.setId(productVo.getCategoryId());
                    product.setCategory(category);
                }

                if (productVo.getImageStr() != null && !productVo.getImageStr().isEmpty()) {
                    String imageUrl = ImageUtil.saveBase64Image(productVo.getImageStr());
                    product.setImageUrl(imageUrl);
                }

            } else {
                product = new Product();
                product.setName(productVo.getName());
                product.setDescription(productVo.getDescription());
                product.setPrice(productVo.getPrice());
                product.setStockQuantity(productVo.getStockQuantity());
                product.setIsActive(productVo.getIsActive());
                product.setCreatedBy(String.valueOf(userId));

                if (productVo.getCategoryId() != null) {
                    Category category = new Category();
                    category.setId(productVo.getCategoryId());
                    product.setCategory(category);
                }

                if (productVo.getImageStr() != null && !productVo.getImageStr().isEmpty()) {
                    String imageUrl = ImageUtil.saveBase64Image(productVo.getImageStr());
                    product.setImageUrl(imageUrl);
                }
            }

            product = productRepository.save(product);
            response.setResponse(DAOResponse.SUCCESS);
            response.setData("product", ProductVo.getAllProducts(product));

        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error occurred while saving/updating product: " + e.getMessage());
            response.setResponse(DAOResponse.INVALID_REQUEST);
        }

        return response;
    }

    public Response getProductById(Long productId) {
        Response response = new Response();
        try {
            Product product = productRepository.getProductById(productId);
            if (product == null) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                return response;
            }
            ProductVo productVo = ProductVo.getAllProducts(product);
            response.setResponse(DAOResponse.SUCCESS);
            response.setData("product", productVo);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error occurred while fetching product by ID: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }
        return response;
    }


    public Response deleteProduct(Long productId) {
        Response response = new Response();
        Long userId = AuthUtils.getCurrentUserId();
        try {
            Product product = productRepository.getProductById(productId);
            if (product == null) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                return response;
            }
            product.setIsActive(false);
            product.setUpdatedBy(String.valueOf(userId));
            productRepository.save(product);
            response.setResponse(DAOResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error occurred while deleting product: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }
            return response;
        }

        //get products by categories and subcategories

    public Response getProductsByCategory(Long categoryId, int pageSize, int pageNumber) {
        Response response = new Response();

        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());

            Category category = categoryRepository.findCategoriesById(categoryId);
            if (category == null) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("Category not found");
                return response;
            }

            // Get all relevant category IDs (including subcategories)
            List<Long> categoryIds = getAllCategoryIds(category);

            // Fetch products by category IDs
            Page<Product> productPage = productRepository.findByCategoryIdIn(categoryIds, pageable);
            List<ProductVo> productVos = productPage.getContent()
                    .stream()
                    .map(ProductVo::getAllProducts)
                    .collect(Collectors.toList());

            if (productVos.isEmpty()) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
            } else {
                response.setResponse(DAOResponse.SUCCESS);
                response.setData("products", productVos);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error occurred while fetching products by category: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }

        return response;
    }

    @Override
    public Response searchProducts(String searchTerm, int pageSize, int pageNumber) {
        Response response = new Response();
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());

            // Use Page instead of List
            Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageable);

            List<ProductVo> productVos = productPage.getContent()
                    .stream()
                    .map(ProductVo::getAllProducts)
                    .collect(Collectors.toList());

            if (!productVos.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("products", productVos);
                result.put("currentPage", productPage.getNumber());
                result.put("totalPages", productPage.getTotalPages());
                result.put("totalItems", productPage.getTotalElements());
                result.put("pageSize", productPage.getSize());
                result.put("hasNext", productPage.hasNext());
                result.put("hasPrevious", productPage.hasPrevious());

                response.setResponse(DAOResponse.SUCCESS);
                response.setData("pagination", result);
            } else {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Error occurred while searching products: " + e.getMessage());
            response.setResponse(DAOResponse.SYSTEM_ERROR);
        }
        return response;
    }




    public List<Long> getAllCategoryIds(Category parent) {
        List<Long> ids = new ArrayList<>();
        ids.add(parent.getId());

        List<Category> subCategories = categoryRepository.findByParentAndActiveTrue(parent);
        for (Category sub : subCategories) {
            ids.addAll(getAllCategoryIds(sub)); // Recursively collect subcategory IDs
        }

        return ids;
    }




}
