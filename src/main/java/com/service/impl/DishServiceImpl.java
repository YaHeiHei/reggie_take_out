package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.dto.DishDto;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.mapper.DishMapper;
import com.service.DishFlavorService;
import com.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存菜品对于的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //把菜品id放入到对于口味的菜品口味中
        List<DishFlavor> flavors = dishDto.getFlavors();
/*        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }*/
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor中
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 更具id查询对应的菜品，并且查询对应的口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //new 一个dishdto对象
        DishDto dishDto = new DishDto();
        //根据id查询对应的菜品信息
        Dish dish = this.getById(id);
        //拷贝dish的信息到dishDto
        BeanUtils.copyProperties(dish, dishDto);
        //根据查询对应的口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 更新菜品信息与菜品对应的口味信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表的基本信息
        this.updateById(dishDto);
        //清理菜品对应口味的信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //把菜品新的口味数据插入口味表中
        List<DishFlavor> flavors = dishDto.getFlavors();
        /*        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }*/
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor中
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品与之对应的口味信息
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        //查询菜品状态是否可以删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId,ids).eq(Dish::getStatus,1);
        int count = (int)this.count(dishLambdaQueryWrapper);
        if(count>0){
            //不能删除抛出业务异常
            throw new CustomException("启售状态无法删除");
        }
        //可以删除走下面方法
        this.removeBatchByIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorlambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorlambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorlambdaQueryWrapper);
    }

    /**
     * 更新菜品对应的状态（启售与停售）
     *
     * @param status
     * @param ids
     */
    @Override
    @Transactional
    public void updateStatus(String status, List<Long> ids) {
        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Dish::getId,ids).set(Dish::getStatus,status);
        this.update(lambdaUpdateWrapper);
    }

}
