package com.yang.test;

import com.yang.tomcat.HttpServlet;
import com.yang.tomcat.annotation.WebServlet;

/**
 * @Author: s2892
 * @Date: 2020/7/16 19:26
 */

@WebServlet("/favicon.ico")
public class Favicon_ico implements HttpServlet {
    @Override
    public void doGet() {

    }

    @Override
    public void doPost() {

    }
}
