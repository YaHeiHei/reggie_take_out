package com.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录ID
 * ThreadLocal会为每一个线程提供一个单独的存储空间，具有线程隔离效果，只有在线程内才能获取值，线程外无法访问，所以不用担心传入数据会弄混掉
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置ID值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 输出ID值
     * @return
     */
    public static Long getCurrentId(){
       return threadLocal.get();
    }
}
