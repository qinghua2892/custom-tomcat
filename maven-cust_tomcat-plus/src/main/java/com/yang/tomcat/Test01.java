package com.yang.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: s2892
 * @Date: 2020/7/16 15:34
 */

public class Test01 {

    public static HashMap<String, HttpServlet> stringHttpServletHashMap;

    private static ServerSocket serverSocket;
    private static HttpReqAndResp httpReqAndResp;
    private static ExecutorService executorService;

    private Test01() {
        try {
            serverSocket = new ServerSocket(8080);
            // 扫描
            TomcatScan tomcatScan = new TomcatScan();
            // 获取到一个以访问路径为key,对应类对象为val的Map集合
            stringHttpServletHashMap = tomcatScan.servletScan();
            // 创建HttpReqAndResp类,需要上面一行代码获取到的Map集合
            httpReqAndResp = new HttpReqAndResp(stringHttpServletHashMap);
            // 创建一个定长的线程池
            executorService = Executors.newFixedThreadPool(3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) throws Exception {
        Test01 test01 = new Test01();
        test01.start();

    }

    public void start() throws IOException {
        while (true) {
            final Socket accept = serverSocket.accept();
            executorService.execute(new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName());
                    httpReqAndResp.request(accept);
                    httpReqAndResp.response(accept);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }, "A"));
        }


    }


}
