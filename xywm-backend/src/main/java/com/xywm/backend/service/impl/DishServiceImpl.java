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
import com.xywm.backend.utils.UserContext; // 补充了这里的导包
import com.xywm.backend.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
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

        // 【关键修复】从上下文中获取商家ID并赋值给菜品实体
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

    private DishVO toVO(Dish dish) {
        DishVO vo = new DishVO();
        BeanUtils.copyProperties(dish, vo);
        Category category = categoryService.getById(dish.getCategoryId());
        if (category != null) vo.setCategoryName(category.getName());
        return vo;
    }
}