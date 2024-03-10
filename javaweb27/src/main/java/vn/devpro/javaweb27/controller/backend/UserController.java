package vn.devpro.javaweb27.controller.backend;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.devpro.javaweb27.controller.BaseController;
import vn.devpro.javaweb27.model.Category;
import vn.devpro.javaweb27.model.Role;
import vn.devpro.javaweb27.model.User;
import vn.devpro.javaweb27.service.RoleService;
import vn.devpro.javaweb27.service.UserService;

@Controller
@RequestMapping(value = "/admin/user/")
public class UserController extends BaseController{
	
	@Autowired
	private UserService userService;
	
	@Autowired RoleService roleService;
	
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(final Model model) {


		List<User> users = userService.findAllActive();
		model.addAttribute("users", users);

		return "backend/user-list";
	}
	
	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String add(final Model model) {
		
		List<Role> roles = roleService.findAllActive();
		model.addAttribute("roles",roles);

		List<User> users = userService.findAllActive();
		model.addAttribute("users", users);

		User user = new User();
		user.setCreateDate(new Date());

		model.addAttribute("user", user);

		return "backend/user-add";
	}

	@RequestMapping(value = "add-save", method = RequestMethod.POST)
	public String addSave(final Model model, @ModelAttribute("user") User user) {

		userService.saveOrUpdate(user);

		model.addAttribute("category", user);

		return "redirect:/admin/user/list";
	}
	
	@RequestMapping(value = "edit/{userId}", method = RequestMethod.GET)
	public String edit(final Model model, @PathVariable("userId") int userId) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

//		Lấy category trong DB bằng id
		User user = userService.getByID(userId);
		model.addAttribute("user", user);

		return "backend/user-edit";
	}

	@RequestMapping(value = "edit-save", method = RequestMethod.POST)
	public String editSave(final Model model, @ModelAttribute("user") User user) {

		userService.saveOrUpdate(user);

		return "redirect:/admin/user/list";
	}
	
	@RequestMapping(value = "delete/{userId}", method = RequestMethod.GET)
	public String delete(final Model model, @PathVariable("userId") int userId) {

//		Lấy category trong DB bằng id
		User user = userService.getByID(userId);
		user.setStatus(false);
		userService.inactiveUser(user);

		return "redirect:/admin/user/list";
	}

}
