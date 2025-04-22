package com.wxl.webstore.cart.dto;

import com.wxl.webstore.cart.entity.Cart;
import com.wxl.webstore.product.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartDTO {
    private String cartId;
    private String userId;
    private String productId;
    private Integer quantity;
    private Boolean selected;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;

    public CartDTO(Cart cart, Product product) {
        this.cartId = String.valueOf(cart.getId());
        this.userId = String.valueOf(cart.getUserId());
        this.productId = String.valueOf(cart.getProductId());
        this.quantity = cart.getQuantity();
        this.selected = cart.getSelected();
        if (product != null) {
            this.productName = product.getName();
            this.productPrice = product.getPrice();
            this.productImage = product.getImgurl();
        }
    }
}
