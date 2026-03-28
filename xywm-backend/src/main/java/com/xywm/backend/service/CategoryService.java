package com.xywm.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xywm.backend.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getPlatformCategories();
    List<Category> getMerchantCategories(Long merchantId);
}