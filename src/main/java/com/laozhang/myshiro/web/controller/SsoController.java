package com.laozhang.myshiro.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.laozhang.myshiro.entity.User;
import com.laozhang.myshiro.service.PasswordHelper;
import com.laozhang.myshiro.service.UserService;
import com.laozhang.myshiro.web.sso.JVMCache;
/**
 * 单点登录
 * @author zhangchengfu
 *
 */
@Controller
@RequestMapping("/sso")
public class SsoController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private PasswordHelper passwordHelper;
	
	@ResponseBody
	@RequestMapping("/test")
	public String test() {
		return "test";
	}
	
	
	@RequestMapping("/index")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		String service = request.getParameter("service");//url地址
		String _service = (String) request.getAttribute("service");
		if (StringUtils.isEmpty(service)) {
			model.addAttribute("service", _service);
		} else {
			model.addAttribute("service", service);
		}
		return "sso/index";
	}
	
	@ResponseBody
	@RequestMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException {
		String username = request.getParameter("username");//用户名
        String password = request.getParameter("password");//密码
        String service = request.getParameter("service");//url地址
        
        User user = userService.findByUsername(username);//查询用户
        
        if (null != user) {
        	String encPassword = passwordHelper.getEncryptPassword(user,password);//密码加密
        	if (encPassword.equals(user.getPassword())) {
        		Cookie cookie = new Cookie("sso", username);
        		cookie.setPath("/");
        		response.addCookie(cookie);
        		
        		long time = System.currentTimeMillis();
        		String timeString = username + time;
        		JVMCache.TICKET_AND_NAME.put(timeString, username);
        		
        		if (!StringUtils.isEmpty(service)) {
        			StringBuilder url = new StringBuilder();
                    url.append(service);
                    if (0 <= service.indexOf("?")) {
                        url.append("&");
                    } else {
                        url.append("?");
                    }
                    url.append("ticket=").append(timeString);
                    response.sendRedirect(url.toString());
        		} else {
        			response.sendRedirect(request.getContextPath() + "/sso/index");//继续登录
        		}
        		
        	} else {
        		//model.addAttribute("service", service);
        		response.sendRedirect(request.getContextPath() + "/sso/index?service=" + service);//继续登录
        	}
        } else {
        	//model.addAttribute("service", service);
        	response.sendRedirect(request.getContextPath() + "/sso/index?service=" + service);//继续登录
        }
	}
	
	@ResponseBody
	@RequestMapping("/ticket")
	public void ticket(HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException {
		String ticket = request.getParameter("ticket");
        String username = JVMCache.TICKET_AND_NAME.get(ticket);
        JVMCache.TICKET_AND_NAME.remove(ticket);
        PrintWriter writer = response.getWriter();
        writer.write(username);
	}
	
}
