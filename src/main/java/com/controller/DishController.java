package com.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.DishDto;
import com.entity.Category;
import com.entity.Dish;
import com.entity.DishFlavor;
import com.service.CategoryService;
import com.service.DishFlavorService;
import com.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 菜品分页查询，可加name条件
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize,String name){
        //分页构造器
        Page<Dish>  pageIn = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加name条件
        dishLambdaQueryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //进行分页查询
        dishService.page(pageIn,dishLambdaQueryWrapper);
        //对象拷贝，因为Dish这个属性里缺少categoryName属性值所以要把分页查询出来的Dish拷贝到DishDto中
        Page<DishDto> pageInFo = new Page<>();
        BeanUtils.copyProperties(pageIn,pageInFo,"records"); //第一个参数是被拷贝对象，第二个是拷贝对象，第三个是被拷贝对象2中需要忽略的属性
        //处理page中封装数据的Records
        List<Dish> records = pageIn.getRecords();
/*        for (Dish record : records) {
            DishDto dishDto= new DishDto();
            BeanUtils.copyProperties(record,dishDto);//把对应的属性拷贝进去
            Long categoryId = record.getCategoryId();//获取分类id
            Category category = categoryService.getById(categoryId);//得到分类对象
            String categoryName = category.getName();//拿到分类的名字
            dishDto.setCategoryName(categoryName);//把对应的分类名称放进去
        }*/
       List<DishDto> list =  records.stream().map((item)->{
            DishDto dishDto= new DishDto();
            BeanUtils.copyProperties(item,dishDto);//把对应的属性拷贝进去
            Long categoryId = item.getCategoryId();//获取分类id
            Category category = categoryService.getById(categoryId);//得到分类对象
            String categoryName = category.getName();//拿到分类的名字
            dishDto.setCategoryName(categoryName);//把对应的分类名称放进去
           return dishDto;
        }).collect(Collectors.toList());
        pageInFo.setRecords(list);
        return R.success(pageInFo);
    }

    /**
     * 根据id查询菜品信息与口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> geiById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品与口味信息
     * @param dishDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 根据id删除菜品与对应的口味信息
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id修改菜品状态，批量也可以单个
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable String status,@RequestParam List<Long> ids){
        dishService.updateStatus(status,ids);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询对应的菜品信息
     * @param dish
     * @return
     */
/*    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> dishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishlambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        dishlambdaQueryWrapper.eq(Dish::getStatus,1);
        dishlambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(dishlambdaQueryWrapper);
        List<DishDto> dishDtoList= dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}
