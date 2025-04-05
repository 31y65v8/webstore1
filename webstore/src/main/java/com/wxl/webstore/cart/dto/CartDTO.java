package com.wxl.webstore.cart.dto;

import com.wxl.webstore.cart.entity.Cart;
import com.wxl.webstore.product.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartDTO {
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Boolean selected;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;

    public CartDTO(Cart cart, Product product) {
        this.cartId = cart.getId();
        this.userId = cart.getUserId();
        this.productId = cart.getProductId();
        this.quantity = cart.getQuantity();
        this.selected = cart.getSelected() == false;
        if (product != null) {
            this.productName = product.getName();
            this.productPrice = product.getPrice();
            this.productImage = product.getImgurl();
        }
    }
}
