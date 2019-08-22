package com.zzx.config;


import com.zzx.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SwaggerConfig swaggerConfig;

    @Autowired
    private ImgUploadConfig imgUploadConfig;

    @Autowired
    private FileUtil fileUtil;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //是否将swagger-ui.html 添加 到 resources目录下
        if (swaggerConfig.isEnableSwagger()) {
            registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");

            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");

            registry.addResourceHandler("/web_frontend/**")
                    .addResourceLocations("classpath:/web_frontend/");
        }

        //以下注释部分用于生产环境下，文件夹单位较大，层数较多的情况
        //文件上传配置
        File root = new File(imgUploadConfig.getUploadFolder());
        ArrayList<File> files = null;
        if (root.exists()) {//有此文件夹默认上传文件夹初始化过
            //将最下层文件夹进行资源映射
            files = new ArrayList<>(fileUtil.getAllFolder());
        } else { //没有被初始化过
            files = new ArrayList<>(fileUtil.initUploadFolder());
        }
        // 将生成的文件夹进行资源映射
        ConcurrentLinkedQueue<File> availablePath = ImgUploadConfig.getAvailablePath();
        String[] paths = new String[files.size()];
        for (int i = 0; i < paths.length; i++) {
            File file = files.get(i);
            paths[i] = "file:" + file.getPath() + "/";
            if (file.listFiles().length < imgUploadConfig.getFolderSize())
                availablePath.add(file);
        }
        registry.addResourceHandler(imgUploadConfig.getStaticAccessPath())
                .addResourceLocations(paths);

    }


}
