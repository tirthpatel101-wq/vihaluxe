package com.vihaluxe.service;

import com.vihaluxe.model.Product;
import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    List<Product> getAllNormalProducts();

    Product getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);

}