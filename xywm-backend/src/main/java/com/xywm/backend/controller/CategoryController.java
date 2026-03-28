package com.xywm.backend.controller;

import com.xywm.backend.common.BusinessException;
import com.xywm.backend.common.Result;
import com.xywm.backend.entity.Category;
import com.xywm.backend.service.CategoryService;
import com.xywm.backend.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getCurrentMerchantCategories(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("currentUserId");
        if (merchantId == null) merchantId = UserContext.getUserId();
        return Result.success(categoryService.getMerchantCategories(merchantId));
    }

    @GetMapping("/platform")
    public Result<List<Category>> getPlatformCategories() {
        return Result.success(categoryService.getPlatformCategories());
    }

    @GetMapping("/merchant/{merchantId}")
    public Result<List<Category>> getMerchantCategories(@PathVariable Long merchantId) {
        return Result.success(categoryService.getMerchantCategories(merchantId));
    }

    @PostMapping("/platform")
    public Result<Void> addPlatformCategory(@RequestBody Category category) {
        category.setType(0);
        category.setMerchantId(0L);
        category.setParentId(0L);
        categoryService.save(category);
        return Result.success();
    }

    @DeleteMapping("/platform/{id}")
    public Result<Void> deletePlatformCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }

    // 商家新增子分类
    @PostMapping("/merchant")
    public Result<Void> addMerchantCategory(@RequestBody Category category, HttpServletRequest request) {
        // 从 Request 中安全提取，不再依赖缓存
        Long merchantId = (Long) request.getAttribute("currentUserId");
        if (merchantId == null) merchantId = UserContext.getUserId();

        if (merchantId == null || merchantId == 0) {
            throw new BusinessException("未能获取到商家身份信息，请重新登录！");
        }

        category.setType(1);
        category.setMerchantId(merchantId);
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping("/merchant")
    public Result<Void> updateMerchantCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/merchant/{id}")
    public Result<Void> deleteMerchantCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}