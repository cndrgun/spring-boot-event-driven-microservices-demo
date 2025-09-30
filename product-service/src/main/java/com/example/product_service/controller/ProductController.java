package com.example.product_service.controller;

import com.example.product_service.dto.DtoProduct;
import com.example.product_service.dto.DtoProductIU;
import com.example.product_service.model.Product;
import com.example.product_service.service.ProductService;
import com.example.product_service.service.S3Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product create(@ModelAttribute DtoProductIU dtoProduct) {

        return productService.createProduct(dtoProduct);

    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean exists = productService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoProduct> getProduct(@PathVariable Long id) {
        DtoProduct dto = productService.getById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

}
