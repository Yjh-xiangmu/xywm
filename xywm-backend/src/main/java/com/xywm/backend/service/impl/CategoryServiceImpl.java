package com.xywm.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xywm.backend.entity.Category;
import com.xywm.backend.mapper.CategoryMapper;
import com.xywm.backend.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getPlatformCategories() {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, 0)
                .orderByAsc(Category::getSort));
    }

    @Override
    public List<Category> getMerchantCategories(Long merchantId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .eq(Category::getType, 1)
                .orderByAsc(Category::getSort));
    }
}