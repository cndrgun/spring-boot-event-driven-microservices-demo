package com.example.product_service.service;

import com.example.product_service.dto.DtoProduct;
import com.example.product_service.dto.DtoProductIU;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Service s3Service;

    public ProductService(ProductRepository productRepository, S3Service s3Service) {
        this.productRepository = productRepository;
        this.s3Service = s3Service;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product save(Product p) {
        return productRepository.save(p);
    }
    public Product createProduct(DtoProductIU dtoProduct) {

        // 1. S3'e yükle (image varsa)
        String imageUrl = null;
        MultipartFile image = dtoProduct.getImage();
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);
        }
        // 2. DTO'dan entity'ye map et
        Product product = new Product();
        product.setName(dtoProduct.getName());
        product.setDescription(dtoProduct.getDescription());
        product.setPrice(dtoProduct.getPrice());
        product.setStock(dtoProduct.getStock());
        product.setPhotoUri(imageUrl);

        // 3. DB'ye kaydet
        return productRepository.save(product);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    public DtoProduct getById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return null;

        DtoProduct dtoProduct = new DtoProduct();
        BeanUtils.copyProperties(product, dtoProduct);

        return dtoProduct;
    }

    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulanamadı: " + productId));

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new RuntimeException("Ürün stoğu bittiğinden güncellenememiştir: " + productId);
        }

        product.setStock(newStock);
        productRepository.save(product);
        System.out.println(">>> Stock updated for productId=" + productId + ", newStock=" + newStock);
    }
}
