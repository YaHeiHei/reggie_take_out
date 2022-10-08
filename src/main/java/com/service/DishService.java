package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dto.DishDto;
import com.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表 ，dish,dishflavor
    public void saveWithFlavor(DishDto dishDto);

    //更具ID查询菜品，同时查询菜品对应的口味数据，需要操作两张表 ，dish,dishflavor
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新菜品对应的口味信息，需要操作两张表 ，dish,dishflavor
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品信息，同时删除菜品对应的口味信息，需要操作两张表 ，dish,dishflavor
    public void deleteWithFlavor(List<Long> ids);

    //更新菜品状态
    public void updateStatus(String status, List<Long> ids);

}
