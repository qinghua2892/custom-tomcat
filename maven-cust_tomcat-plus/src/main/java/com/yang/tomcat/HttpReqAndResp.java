package com.yang.tomcat;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * @Author: s2892
 * @Date: 2020/7/16 21:21
 */

public class HttpReqAndResp {

    public static HashMap<String, HttpServlet> stringHttpServletMap;

    public HttpReqAndResp(HashMap<String, HttpServlet> _stringHttpServletMap) {
        stringHttpServletMap = _stringHttpServletMap;
    }

    /**
     * 响应202状态码
     *
     * @param socket
     * @throws IOException
     */
    public void httpAccepted(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        //服务器收到浏览器的请求之后，需要返回202，之后才能读取数据
        String begin = "HTTP/1.1 202 Accepted\n" +
                // "Location: http://www.baidu.com\n"+
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                "Server: Apache\n";
        outputStream.write(begin.getBytes());
        outputStream.flush();
    }

    /**
     * 请求
     *
     * @param socket
     * @throws IOException
     */
    public void request(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[1024];
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
        HttpProtocol httpProtocol = parseHttpStr(stringBuilder.toString());
        HttpServlet httpServlet = stringHttpServletMap.get(httpProtocol.getServletUri());

        if ("GET".equalsIgnoreCase(httpProtocol.getRequestMethod())) {
            httpServlet.doGet();
        }

        if ("POST".equalsIgnoreCase(httpProtocol.getRequestMethod())) {
            httpServlet.doPost();
        }

    }


    /**
     * 响应
     *
     * @param socket
     * @throws IOException
     */
    public void response(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        String body = "<html><head></head><body><h1>傻逼,自己人!</h1></body></html>";

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


    }

    /**
     * 通过请求头获取到请求头信息对象   请求方法   请求路径    请求路径的uri
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
