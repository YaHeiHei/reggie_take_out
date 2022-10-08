package com.controller;


import com.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 实现文件上传与下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    //获取配置文件中的目录
    @Value("${uploadfile.path}")
    private String path;

    /**
     * 实现上传功能
     *
     * @param file MultipartFile file  MultipartFile这个方法是springboot.web提供的用来简化文件的上传与下载， file这个名字必须与前端表单的name相同
     * @return
     */
    @PostMapping("/upload")
    public R<String> upLoad(MultipartFile file) throws IOException {
        //获取上传文件文件名称
        String fileName = file.getOriginalFilename();
        //获取上传文件名的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //使用uuid工具类生成一个uuid作为上传文件的新文件名，以防止有相同名称的文件上传，覆盖前一个文件
        String uuid = UUID.randomUUID().toString();
        //新的文件名
        String newFlie = uuid + suffix;
        //判断path目录存不存在
        //创建一个目录对象
        File dir = new File(path);
        //如果目录不存在则创捷一个
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //把上传的文件转存到固定的位置，文件名称使用uuid加上原文件名称后缀
        file.transferTo(new File(path + newFlie));
        return R.success(newFlie);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(path + name));
            //输出流输出到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应文件格式
            response.setContentType("image/jpeg");
            //定义一个数组，做为缓冲区
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
