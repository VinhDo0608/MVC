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
import vn.devpro.javaweb27.model.Role;
import vn.devpro.javaweb27.model.User;
import vn.devpro.javaweb27.service.RoleService;
import vn.devpro.javaweb27.service.UserService;

@Controller
@RequestMapping(value = "/admin/role/")
public class RoleController extends BaseController{
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(final Model model) {


		List<Role> roles = roleService.findAllActive();
		model.addAttribute("roles", roles);

		return "backend/role-list";
	}
	
	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String add(final Model model) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

		Role role = new Role();
		role.setCreateDate(new Date());

		model.addAttribute("role", role);

		return "backend/role-add";
	}

	@RequestMapping(value = "add-save", method = RequestMethod.POST)
	public String addSave(final Model model, @ModelAttribute("role") Role role) {

		roleService.saveOrUpdate(role);

		model.addAttribute("role", role);

		return "redirect:/admin/role/list";
	}

	@RequestMapping(value = "edit/{roleId}", method = RequestMethod.GET)
	public String edit(final Model model, @PathVariable("roleId") int roleId) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

//		Lấy category trong DB bằng id
		Role role = roleService.getByID(roleId);
		model.addAttribute("role", role);

		return "backend/role-edit";
	}

	@RequestMapping(value = "edit-save", method = RequestMethod.POST)
	public String editSave(final Model model, @ModelAttribute("role") Role role) {

		roleService.saveOrUpdate(role);

		return "redirect:/admin/role/list";
	}

	@RequestMapping(value = "delete/{categoryId}", method = RequestMethod.GET)
	public String delete(final Model model, @PathVariable("categoryId") int roleId) {

//		Lấy category trong DB bằng id
		Role role = roleService.getByID(roleId);
		role.setStatus(false);
		roleService.inactiveCategory(role);

		return "redirect:/admin/role/list";
	}
}
