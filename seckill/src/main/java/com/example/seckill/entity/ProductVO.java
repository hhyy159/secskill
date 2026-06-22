package com.example.seckill.entity;

//2026.5.30 余浩洋 商品展示VO，包含动态计算的状态字段
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductVO {
    private int product_id;
    private String product_name;
    private String description;
    private int stock;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String status;       // 未开始 / 进行中 / 已售罄 / 已结束

    /**
     * 将Product实体转换为带状态的前端展示VO
     */
    public static ProductVO fromProduct(Product product) {
        ProductVO vo = new ProductVO();
        vo.setProduct_id(product.getProductId());
        vo.setProduct_name(product.getProductName());
        vo.setDescription(product.getDescription());
        vo.setStock(product.getStock());
        vo.setStart_time(product.getStartTime());
        vo.setEnd_time(product.getEndTime());
        vo.setStatus(computeStatus(product));
        return vo;
    }

    /**
     * 根据当前系统时间与商品时间窗口、库存计算状态
     */
    private static String computeStatus(Product product) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(product.getStartTime())) {
            return "未开始";
        } else if (now.isAfter(product.getEndTime())) {
            return "已结束";
        } else if (product.getStock() <= 0) {
            return "已售罄";
        } else {
            return "进行中";
        }
    }
}
