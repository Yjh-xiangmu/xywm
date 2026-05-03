package com.xywm.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xywm.backend.dto.DishDTO;
import com.xywm.backend.entity.Dish;
import com.xywm.backend.vo.DishVO;

import java.util.List;
import java.util.Map;

public interface DishService extends IService<Dish> {
    List<DishVO> getMerchantDishes(Long merchantId);
    List<DishVO> getDishesByCategory(Long categoryId);
    void saveDish(DishDTO dto);
    void updateDish(DishDTO dto);
    void updateStatus(Long id, Integer status);

    // 新增：按分类分组返回菜品（用于用户端商家详情页）
    List<Map<String, Object>> getGroupedDishes(Long merchantId);
}