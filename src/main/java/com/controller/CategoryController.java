package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.entity.Category;
import com.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品与套餐
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category){
        if (category!=null){
            categoryService.save(category);
        }
        return R.success("新增成功");
    }

    /**
     * 菜品分类信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize){
        //分页构造器
        Page<Category> pageIn = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categoryService.page(pageIn,categoryLambdaQueryWrapper);
        return R.success(pageIn);
    }

    @DeleteMapping()
    public R<String> delete(@RequestParam Long ids){
        //自己定义的根据id删除分类的方法，该方法在删除前会判断是否有菜品或者套餐与其关联如果有则不允许删除，抛出自定义异常传递给前端提示信息
        categoryService.removeByIdAndJudge(ids);
        return R.success("删除成功");
    }

    /**
     * 根据ID进行修改分类信息
     * @param category
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("更新成功");
    }

    /**
     * 更具条件分类查询数据
     * @param category
     * @return
     */
    @GetMapping("list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
