package com.yang.tomcat;

import com.yang.tomcat.annotation.WebServlet;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

/**
 * @Author: s2892
 * @Date: 2020/7/16 19:47
 */

public class TomcatScan {

    public HashMap<String, HttpServlet> servletScan() throws Exception {
        HashMap<String, HttpServlet> servletMap = new HashMap<>();
        String packBao = "com.yang.test";
        URL resource = TomcatScan.class.getResource("/");
        String path = packBao.replaceAll("\\.", "/");
        // 获取当前项目的classpath绝对路径
        File fileAll = new File(resource.getPath() + path);
        // 获取到所有文件
        File[] files = fileAll.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                // 为文件
                if (file.getName().endsWith(".class")) {
                    // class结尾的
                    String[] split = file.getName().split("\\.");
                    // 类的全限定类名
                    String tclassName = packBao + "." + split[0];
                    Class<?> aClass = Class.forName(tclassName);
                    WebServlet annotation = aClass.getAnnotation(WebServlet.class);
                    if (annotation != null) {
                        String value = annotation.value();
                        // 防止出现一样的访问路径
                        if (servletMap.get(value) != null) {
                            throw new Exception("出现了两个一样的地址：" + value);
                        }
                        // 类的key  也是访问路径
                        servletMap.put(value, (HttpServlet) aClass.newInstance());
                    }
                }
            } else {
                // 文件夹
            }
        }


        return servletMap;
    }
}
