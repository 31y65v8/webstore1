package com.wxl.webstore.product.mapper;

import com.wxl.webstore.product.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 商品信息表 Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-01
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {


    // 按照商品名称查询
    Product selectByName(String name);

    // 按照卖家ID查询商品
    //List<Product> selectBySellerId(Long sellerId);

    // 按照卖家ID和分类查询商品
    //List<Product> selectBySellerIdAndCategory(Long sellerId, ProductCategory category);

    // 查询所有商品
    //List<Product> selectAll();

    // 按照商品分类查询商品
    //List<Product> selectByCategory(String category);

    // 根据是否删除标志查询商品
    //List<Product> selectByIsDeleted(Integer isDeleted);

    // 分页查询所有商品
    Page<Product> selectPageAll(Page<Product> page);

    // 按分类分页查询商品
    //Page<Product> selectPageByCategory(Page<Product> page, @Param("category") ProductCategory category);


    // 根据商品名称模糊查询并分页
    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{name}, '%')")
    Page<Product> selectPageByName(Page<Product> page, @Param("name") String name);

    List<Long> selectProductIdsByCategoryAndPriceRange(@Param("category") String category,
                                                       @Param("minPrice") BigDecimal minPrice,
                                                       @Param("maxPrice") BigDecimal maxPrice);

    @Select("SELECT DISTINCT seller_id FROM product WHERE is_deleted = 0")
    List<Long> selectDistinctSellerIds();

    @Select("SELECT DISTINCT category FROM product WHERE is_deleted = 0")
    List<String> selectAllCategories();

    @Select("SELECT id FROM product WHERE category = #{category} AND is_deleted = 0")
    List<Long> selectIdsByCategory(@Param("category") String category);

    @Update("UPDATE product SET is_deleted = 1 WHERE id = #{id}")
    int updateIsDeleteById(@Param("id") Long id);

    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{id}")
    int updatesales(@Param("id") Long id,@Param("quantity") int quantity);

}
