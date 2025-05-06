package com.wxl.webstore.productsalestrend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wxl.webstore.log.purchaselog.mapper.PurchaseLogMapper;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.mapper.ProductMapper;
import com.wxl.webstore.productsalestrend.dto.ProductSalesTrendDTO;

import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.formula.Formula;
import smile.data.vector.BaseVector;
import smile.data.vector.IntVector;
import smile.regression.OLS;
import smile.regression.LinearModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductSalesTrendService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PurchaseLogMapper purchaseLogMapper;

    public static double predictNextMonth(List<Integer> sales) {
        int n = sales.size();
        if (n < 2) return sales.isEmpty() ? 0 : sales.get(n - 1);
    
        // 构造 DataFrame：x（时间） 和 y（销量）
        int[] x = new int[n];
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = i + 1;
            y[i] = sales.get(i);
        }
    
        DataFrame df = DataFrame.of(
            IntVector.of("x", x),
            IntVector.of("y", y)
        );
    
        // 使用 OLS 回归，y 是目标变量，x 是特征
        LinearModel model = OLS.fit(Formula.lhs("y"), df);
    
        // 预测下一个月（第 n+1 月）的销量
        //Tuple newPoint = BaseVector.of("x", new int[]{n + 1}).get(0);
        Tuple newPoint = DataFrame.of(IntVector.of("x", new int[]{n + 1})).get(0);

        return model.predict(newPoint);
    }


    // 趋势评估
    public static String evaluateTrend(List<Integer> sales) {
        if (sales.size() < 2) return "数据不足";
        double pred = predictNextMonth(sales);
        double last = sales.get(sales.size() - 1);
        double growth = pred - last;
        if (growth > 0.05 * last) return "上升";
        if (growth < -0.05 * last) return "下降";
        return "平稳";
    }

    // 异常判别（3σ原则）
    public static boolean isAnomaly(List<Integer> sales, int current) {
        double mean = sales.stream().mapToInt(Integer::intValue).average().orElse(0);
        double std = Math.sqrt(sales.stream().mapToDouble(i -> Math.pow(i - mean, 2)).average().orElse(0));
        return current > mean + 3 * std || current < mean - 3 * std;
    }

    // 获取单个商品销售趋势
    public ProductSalesTrendDTO getProductSalesTrend(Long productId) {
        Product product = productMapper.selectById(productId);
        List<Map<String, Object>> salesData = purchaseLogMapper.selectMonthlySalesByProductId(productId);
        List<Integer> monthlySales = salesData.stream()
            .map(m -> ((Number)m.get("total")).intValue())
            .collect(Collectors.toList());
        double nextMonth = predictNextMonth(monthlySales);
        String trend = evaluateTrend(monthlySales);
        int currentMonthSales = monthlySales.isEmpty() ? 0 : monthlySales.get(monthlySales.size() - 1);
        boolean anomaly = isAnomaly(monthlySales, currentMonthSales);

        ProductSalesTrendDTO dto = new ProductSalesTrendDTO();
        dto.setProductId(productId);
        dto.setProductName(product.getName());
        dto.setMonthlySales(monthlySales);
        dto.setNextMonthPrediction(nextMonth);
        dto.setTrend(trend);
        dto.setCurrentMonthSales(currentMonthSales);
        dto.setIsAnomaly(anomaly);
        return dto;
    }

    // 获取所有商品趋势
    public List<ProductSalesTrendDTO> getAllProductTrends() {
        List<Product> products = productMapper.selectList(null);
        List<ProductSalesTrendDTO> result = new ArrayList<>();
        for (Product p : products) {
            result.add(getProductSalesTrend(p.getId()));
        }
        return result;
    }
}
