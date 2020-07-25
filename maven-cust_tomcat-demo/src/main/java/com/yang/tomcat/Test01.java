package com.yang.tomcat;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * @Author: s2892
 * @Date: 2020/7/16 15:34
 */

public class Test01 {

    public static HashMap<String, HttpServlet> stringHttpServletHashMap;


    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);
        TomcatScan tomcatScan = new TomcatScan();
        stringHttpServletHashMap = tomcatScan.servletScan();

        while (true) {

            //每来一个请求都会有一个socket对象被创建
            //当没有请求的时候，会一致阻塞在此处
            Socket socket = serverSocket.accept();
            System.out.println(socket);

            OutputStream outputStream = socket.getOutputStream();
            //从输入流中可以读取到浏览器发给我们的消息
            InputStream inputStream = socket.getInputStream();
//
//            //服务器收到浏览器的请求之后，需要返回202，之后才能读取数据
//            String begin = "HTTP/1.1 202 Accepted\n" +
//                    // "Location: http://www.baidu.com\n"+
//                    "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
//                    "Server: Apache\n";
//            outputStream.write(begin.getBytes());
//            outputStream.flush();


            //-----读取浏览器请求的数据
            //用来存放input读取到的数据
            byte[] buffer = new byte[1024];
            //用来记录读取了多少个字节
            int len = 0;

            StringBuilder stringBuilder = new StringBuilder();
            //如果没有数据了，input会返回一个-1
            while (true) {
                len = inputStream.read(buffer);
                //浏览器如果不关闭，此处的流就会一直等待
                stringBuilder.append(new String(buffer, 0, len));
                if (len < 1024) {
                    break;
                }
            }
            System.out.println(stringBuilder);
            // 通过请求获取到请求封装对象
            HttpProtocol httpProtocol = parseHttpStr(stringBuilder.toString());
            // 请求访问的路径
            String servletUri = httpProtocol.getServletUri();

            // 通过请求获取的类
            HttpServlet httpServlet = stringHttpServletHashMap.get(servletUri);

            //进行Get和Post请求分发
            if ("Get".toUpperCase().equals(httpProtocol.getRequestMethod())) {
                //Get请求
                httpServlet.doGet(); //接口回调


            } else if ("Post".toUpperCase().equals(httpProtocol.getRequestMethod())) {
                //Post请求
                httpServlet.doPost();//接口回调
            }

            //---响应
            String body = "<html><head></head><body><h1>HelloWorld</h1></body></html>";

            String str = "HTTP/1.1 200 OK\n" +
                    // "Location: http://www.baidu.com\n"+
                    "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                    "Server: Apache\n" +
                    "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                    "ETag: \"34aa387-d-1568eb00\"\n" +
                    "Accept-Ranges: bytes\n" +
                    "Content-Length: " + body.length() + "\n" +
                    "Vary: Accept-Encoding\n" +
                    "Content-Type: text/html\n" +
                    "\n" + body;
            outputStream.write(str.getBytes());
            outputStream.flush();

            inputStream.close();
            outputStream.close();


        }

    }

    /**
     * 获取到请求头的信息   请求方法   请求路径    请求路径的uri
     *
     * @param httpUrl
     * @return
     */
    private static HttpProtocol parseHttpStr(String httpUrl) {
        HttpProtocol httpProtocol = new HttpProtocol();
        String[] split = httpUrl.split("\r\n");
        for (int i = 0; i < split.length; i++) {
            // 获取请求头的第一行信息
            if (i == 0) {
                String[] split1 = split[0].split(" ");
                // 请求路径
                httpProtocol.setUrl(split1[1]);
                // 请求方式
                httpProtocol.setRequestMethod(split1[0]);
                String[] split2 = split1[1].split("\\?");
                // 请求路径的uri
                httpProtocol.setServletUri(split2[0]);
            }
        }

        return httpProtocol;
    }


}
