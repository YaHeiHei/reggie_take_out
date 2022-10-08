package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.dto.SetmealDto;
import com.entity.Category;
import com.entity.Setmeal;
import com.service.CategoryService;
import com.service.SetmealDishService;
import com.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息" + setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询，以及按套餐名称进行分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page, @RequestParam Integer pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageIn = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        lambdaQueryWrapper.eq(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //进行分页查询
        setmealService.page(pageIn, lambdaQueryWrapper);
        //new一个SetmealDto的分页构造器
        Page<SetmealDto> pageInFo = new Page<>();
        //把Setmeal分页构造器中除了records属性的值以外全都拷贝到SetmealDto的分页构造器中
        BeanUtils.copyProperties(pageIn, pageInFo, "records");   //第一个参数是被拷贝对象，第二个是拷贝对象，第三个是被拷贝对象2中需要忽略的属性
        //取出Setmeal分页构造器中查询出来的records属性的值
        List<Setmeal> records = pageIn.getRecords();
        //处理records属性的值数据
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);//把对应的属性拷贝进去
            Long categoryId = item.getCategoryId();//获取套餐分类的Id
            Category category = categoryService.getById(categoryId);//更具套餐分类的Id查处具体的套餐
            String categoryName = category.getName();//获取套餐分类的名称
            setmealDto.setCategoryName(categoryName);//把对应的套餐分类名称放进去
            return setmealDto;
        }).collect(Collectors.toList());
        pageInFo.setRecords(list);//把修改好的查询结果放入pageInFo的records中
        return R.success(pageInFo);
    }

    /**
     * 根据id查询套餐信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> geiById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 根据ID删除套餐以及套餐对应的菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id修改套餐状态，批量也可以单个
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable String status, @RequestParam List<Long> ids) {
        setmealService.updateStatus(status, ids);
        return R.success("修改成功");
    }


    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(lambdaQueryWrapper);
        return R.success(setmealList);
    }
}
