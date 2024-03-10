package vn.devpro.javaweb27.controller;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.devpro.javaweb27.dto.Cart;
import vn.devpro.javaweb27.model.User;

@Controller
public class BaseController {
	@ModelAttribute("title")
	public String projectTitle() {
		return "Javaweb27";
	}
	
	@ModelAttribute("totalCartProducts")
	public BigInteger getTotalCartProducts(final HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("cart") == null) {
			return BigInteger.ZERO;
		}
		
		Cart cart = (Cart)session.getAttribute("cart");
		return cart.totalCartProduct();
	}
	
	// Lấy thông tin của user đăng nhập
	@ModelAttribute("loginedUser")
	public User getLoginedUser() {
		
		Object loginedUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (loginedUser != null && loginedUser instanceof UserDetails) {
			User user = (User) loginedUser;
			return user;
		}
		return new User();
	}
	
	// Kiểm tra đã login hay chưa
	@ModelAttribute("isLogined")
	public boolean isLogined() {
		
		Object loginedUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (loginedUser != null && loginedUser instanceof UserDetails) {
			return true;
		}
		return false;
	}
}
