package com.wxl.webstore.common.enums;

import lombok.Getter;

@Getter
public enum ProductCategory {
    //@EnumValue
    FASHION("FASHION"),//服饰
    HOME("HOME"),      //家居
    FOOD("FOOD"),      //食品
    BEAUTY("BEAUTY"),  //美妆
    BABY("BABY"),      //母婴
    SPORTS("SPORTS"),  //运动
    BOOKS("BOOKS"),    //书籍
    DIGITAL("DIGITAL");//数码

    private final String value;

    ProductCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductCategory fromValue(String value) {
        for (ProductCategory category : values()) {
            if (category.getValue().equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
