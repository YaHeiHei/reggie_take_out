package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 根据ID删除分类，删除之前需要进行判断
     * @param id
     */
    public void removeByIdAndJudge(Long id);
}
