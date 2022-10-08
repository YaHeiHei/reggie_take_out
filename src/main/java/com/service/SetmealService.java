package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.SetmealDto;
import com.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存菜品与套餐的关联信息
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     *删除套餐同时删除对应的菜品信息
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);

    /**
     * 更具id查询对应的套餐以及套餐包含的菜品
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    /**
     * 更新套餐，同时保存菜品与套餐的关联信息
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);

    /**
     * 更新套餐状态
     * @param status
     * @param ids
     */
    public void updateStatus(String status, List<Long> ids);
}
