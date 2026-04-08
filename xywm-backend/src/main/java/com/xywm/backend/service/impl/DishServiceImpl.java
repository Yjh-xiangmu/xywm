package com.xywm.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.common.BusinessException;
import com.xywm.backend.dto.DishDTO;
import com.xywm.backend.entity.Category;
import com.xywm.backend.entity.Dish;
import com.xywm.backend.mapper.DishMapper;
import com.xywm.backend.service.CategoryService;
import com.xywm.backend.service.DishService;
import com.xywm.backend.utils.UserContext;
import com.xywm.backend.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private CategoryService categoryService;

    @Override
    public List<DishVO> getMerchantDishes(Long merchantId) {
        List<Dish> dishes = list(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getMerchantId, merchantId)
                .orderByDesc(Dish::getCreateTime));
        return dishes.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<DishVO> getDishesByCategory(Long categoryId) {
        List<Dish> dishes = list(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1));
        return dishes.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void saveDish(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        Long merchantId = UserContext.getUserId();
        if (merchantId == null) {
            throw new BusinessException("未能获取到商家身份信息");
        }
        dish.setMerchantId(merchantId);

        save(dish);
    }

    @Override
    public void updateDish(DishDTO dto) {
        if (dto.getId() == null) throw new BusinessException("菜品ID不能为空");
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        updateById(dish);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        updateById(dish);
    }

    // 新增：按分类分组返回菜品（用于用户端商家详情页）
    @Override
    public List<Map<String, Object>> getGroupedDishes(Long merchantId) {
        // 获取该商家的所有子分类
        List<Category> categories = categoryService.getMerchantCategories(merchantId);

        // 获取该商家所有上架菜品
        List<DishVO> allDishes = getMerchantDishes(merchantId)
                .stream()
                .filter(d -> d.getStatus() != null && d.getStatus() == 1)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();

        // 按分类归组
        for (Category cat : categories) {
            List<DishVO> dishes = allDishes.stream()
                    .filter(d -> cat.getId().equals(d.getCategoryId()))
                    .collect(Collectors.toList());
            if (!dishes.isEmpty()) {
                Map<String, Object> group = new LinkedHashMap<>();
                group.put("categoryId", cat.getId());
                group.put("categoryName", cat.getName());
                group.put("dishes", dishes);
                result.add(group);
            }
        }

        // 把没有匹配到分类的菜品放到"其他"组
        Set<Long> categorizedIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        List<DishVO> uncategorized = allDishes.stream()
                .filter(d -> !categorizedIds.contains(d.getCategoryId()))
                .collect(Collectors.toList());
        if (!uncategorized.isEmpty()) {
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("categoryId", 0L);
            group.put("categoryName", "其他");
            group.put("dishes", uncategorized);
            result.add(group);
        }

        return result;
    }

    private DishVO toVO(Dish dish) {
        DishVO vo = new DishVO();
        BeanUtils.copyProperties(dish, vo);
        Category category = categoryService.getById(dish.getCategoryId());
        if (category != null) vo.setCategoryName(category.getName());
        return vo;
    }
}