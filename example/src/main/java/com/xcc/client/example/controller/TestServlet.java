package com.xcc.client.example.controller;


import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.servlet.lmp.HttpServlet;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 20:40.
 */

public class TestServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) throws IOException, ServletException {
        response.redirect("/views/hello.html");
    }
}
