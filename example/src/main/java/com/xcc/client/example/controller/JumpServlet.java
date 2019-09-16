package com.xcc.client.example.controller;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.servlet.lmp.HttpServlet;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/11.
 * @time 11:05.
 */

public class JumpServlet extends HttpServlet {

    @Override
    public void doGet(Request request, Response response) throws IOException, ServletException {
        request.getRequestDispatcher("/views/jump.html").forward(request,response);
    }

    @Override
    public void doPost(Request request, Response response) throws IOException, ServletException {
        response.redirect("/hello");
    }
}
