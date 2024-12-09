package com.zs.foms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.foms.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
