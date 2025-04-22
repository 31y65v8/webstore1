package com.wxl.webstore.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.product.dto.ProductUpdateDTO;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.dto.ProductDTO;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品信息表 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-01
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${upload.product-images.path}")
    private String uploadPath;

    // 获取首页商品分页数据
    @GetMapping("/products")
    public Page<ProductDTO> getProductsPage(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        return productService.getPageOfProductDTOs(pageNum, pageSize);
    }

    // 获取按照分类分页显示商品
    @GetMapping("/products/category")
    public Page<ProductDTO> getProductsByCategory(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam ProductCategory category) {
        return productService.getPageByCategoryDTOs(pageNum, pageSize, category);
    }

    // 获取商品名称模糊搜索的分页结果
    @GetMapping("/products/search")
    public Page<ProductDTO> searchProductsByName(@RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam String name) {
        return productService.getProductsByNameDTOs(pageNum, pageSize, name);
    }

    // 卖家上传商品图片
    @PostMapping("/upload/image")
    //@PreAuthorize("hasRole('SELLER')")
    public Result<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                return Result.error("创建上传目录失败");
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";

            // 生成唯一的文件名
            String filename = UUID.randomUUID().toString() + extension;
            File destFile = new File(uploadDir, filename);

            // 保存文件
            file.transferTo(destFile);

            // 返回可访问的URL路径
            return Result.success("/api/product/images/" + filename);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    // 卖家添加新商品
    @PostMapping("/add")
    @PreAuthorize("hasRole('SELLER')")
    public Result<ProductDTO> addProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.addProduct(product);
            return Result.success(new ProductDTO(savedProduct));
        } catch (Exception e) {
            return Result.error("添加商品失败：" + e.getMessage());
        }
    }

    // 获取卖家的商品列表
    @GetMapping("/seller/products")
    @PreAuthorize("hasRole('SELLER')")
    public Result<Page<ProductDTO>> getSellerProducts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<Product> products = productService.getSellerProducts(pageNum, pageSize);
            return Result.success(convertToProductDTOPage(products));
        } catch (Exception e) {
            return Result.error("获取商品列表失败：" + e.getMessage());
        }
    }

    // 更新商品信息
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public Result<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDTO updateDTO) {
        try {
            Product updatedProduct = productService.updateProductPartial(id, updateDTO);
            return Result.success(new ProductDTO(updatedProduct));
        } catch (Exception e) {
            return Result.error("更新商品失败：" + e.getMessage());
        }
    }

    // 删除商品（软删除）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除商品失败：" + e.getMessage());
        }
    }

    // 获取单个商品详情
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public Result<ProductDTO> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return Result.success(new ProductDTO(product));
        } catch (Exception e) {
            return Result.error("获取商品详情失败：" + e.getMessage());
        }
    }

    private Page<ProductDTO> convertToProductDTOPage(Page<Product> productPage) {
        Page<ProductDTO> dtoPage = new Page<>(
                productPage.getCurrent(),
                productPage.getSize(),
                productPage.getTotal());
        
        List<ProductDTO> records = productPage.getRecords().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
        
        dtoPage.setRecords(records);
        return dtoPage;
    }
}
