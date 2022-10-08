package com.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)//mybatisplus提供的操作，代表在插入的时候执行MyMetaObjectHandler这个类里插入对应的操作
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//mybatisplus提供的操作，代表在插入的时候执行MyMetaObjectHandler这个类里插入对应的操作
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)//mybatisplus提供的操作，代表在更新的时候执行MyMetaObjectHandler这个类里更新对应的操作
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//mybatisplus提供的操作，代表在插入和更新的时候执行MyMetaObjectHandler这个类里插入和更新对应的操作
    private Long updateUser;

}
