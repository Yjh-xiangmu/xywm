package com.xywm.backend.controller;

import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.dto.DishDTO;
import com.xywm.backend.service.DishService;
import com.xywm.backend.utils.UserContext;
import com.xywm.backend.vo.DishVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    // 商家端：获取自己的菜品列表（可按分类过滤）
    @GetMapping("/list")
    public Result<List<DishVO>> getCurrentMerchantDishes(@RequestParam(required = false) Long categoryId, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("currentUserId");
        if (merchantId == null) merchantId = UserContext.getUserId();

        List<DishVO> list = dishService.getMerchantDishes(merchantId);
        if (categoryId != null) {
            list = list.stream().filter(dish -> categoryId.equals(dish.getCategoryId())).collect(Collectors.toList());
        }
        return Result.success(list);
    }

    // 用户端：获取指定商家的菜品列表
    @GetMapping("/merchant/{merchantId}")
    public Result<List<DishVO>> getMerchantDishes(@PathVariable Long merchantId) {
        return Result.success(dishService.getMerchantDishes(merchantId));
    }

    // 用户端：获取指定商家的菜品（按分类分组，用于商家详情页）
    @GetMapping("/grouped/{merchantId}")
    public Result<List<Map<String, Object>>> getGroupedDishes(@PathVariable Long merchantId) {
        return Result.success(dishService.getGroupedDishes(merchantId));
    }

    // 商家新增菜品
    @PostMapping
    public Result<Void> addDish(@RequestBody DishDTO dto, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("currentUserId");
        if (merchantId == null) merchantId = UserContext.getUserId();

        if (merchantId == null || merchantId == 0) {
            throw new BusinessException("未能获取到商家身份信息，请重新登录！");
        }

        UserContext.setUserId(merchantId);

        dto.setId(null);
        dishService.saveDish(dto);
        return Result.success();
    }

    // 商家修改菜品
    @PutMapping
    public Result<Void> updateDish(@RequestBody DishDTO dto) {
        dishService.updateDish(dto);
        return Result.success();
    }

    // 商家修改菜品状态（上架/下架）
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        dishService.updateStatus(id, status);
        return Result.success();
    }

    // 商家删除菜品
    @DeleteMapping("/{id}")
    public Result<Void> deleteDish(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success();
    }
}