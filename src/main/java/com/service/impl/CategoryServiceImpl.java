package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CustomException;
import com.entity.Category;
import com.entity.Dish;
import com.entity.Setmeal;
import com.mapper.CategoryMapper;
import com.service.CategoryService;
import com.service.DishService;
import com.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    //菜品service
    @Autowired
    private DishService dishService;

    //套餐service
    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id删除分类，删除之前要进行判断当前分类是否已经关联了其他菜品或者套餐，如果关联则抛出CustomException类我们自己定义的一个异常
     * @param id
     */
    @Override
    public void removeByIdAndJudge(Long id) {

        //查询当前分类是否已经关联了菜品，如果已经关联则不能删除，抛出一个异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //添加查询条件根据id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
            //调用dishService查询看看里面是否有属于该分类的菜品
        int dishCount = (int) dishService.count(dishLambdaQueryWrapper);
        if (dishCount>0){
            //已经关联菜品，抛出CustomException类自定义异常
            throw new CustomException("该分类关联了菜品，不能删除");
        }
        //查询当前分类是否已经关联了套餐，如果已经关联则不能删除，抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getId,id);
            //调用categoryService查询看看里面是否有属于该分类的菜品
        int setmealCount = (int) setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount>0){
            //已经关联套餐，抛出CustomException类自定义异常
            throw new CustomException("该分类关联了套餐，不能删除");
        }

        //没有走上面两个if说明都没有关联直接删除
        super.removeById(id);
    }
}
