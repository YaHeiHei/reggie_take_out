package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.R;
import com.entity.Employee;
import com.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request ,@RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> EmployeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        EmployeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(EmployeeLambdaQueryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败，用户未注册");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败，用户密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus()== 0 ){
            return R.error("该用户已经被禁用");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        String password = "123456";  //因为前端添加用户名字并没有输入密码所以后端给一个默认密码，也就是初始密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());//将密码进行md5加密
        employee.setPassword(password);//将密码存入对象中
//        employee.setCreateTime(LocalDateTime.now());//设置创建时间为当前时间
//        employee.setUpdateTime(LocalDateTime.now());//设置更新时间为当前时间
//        Long empid = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empid);//设置创建人为当前登录人
//        employee.setUpdateUser(empid);//设置更新人为当前登录人
        employeeService.save(employee);//插入数据库
        log.info(employee.toString());
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page 当前页码
     * @param pageSize 一页显示多少条数据
     * @param name 按名称查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page,@RequestParam Integer pageSize,String name){
        //构造分页构造器
        Page<Employee> pageIn = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        lambdaQueryWrapper.like(Strings.isNotEmpty(name),Employee::getName,name);//如果name不为空则加入查询条件
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);//按照更新时间排序
        //调用service层继承IService的方法执行分页查询
        employeeService.page(pageIn,lambdaQueryWrapper);
        return R.success(pageIn );
    }

    /**
     * 更新用户信息，状态与信息都走这个方法
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
//        employee.setUpdateTime(LocalDateTime.now());//设置更新时间
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));//设置更新人。从session中获取，因为存放的时候id是mybatisplus更具雪花算法生成的所以要转成Long型的
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 更具ID查询信息，然后返回给前端，用于更新用户信息的页面展示，方便用户修改。
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
/*        if(employee != null){
            return R.success(employee);
        }*/
        return employee!=null? R.success(employee):R.error("该用户不存在，查询不成功");
        //return R.error("该用户不存在，查询不成功");
    }
}
