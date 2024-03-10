package vn.devpro.javaweb27.controller.backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.devpro.javaweb27.controller.BaseController;
import vn.devpro.javaweb27.dto.Jw27Constant;
import vn.devpro.javaweb27.dto.SearchModel;
import vn.devpro.javaweb27.model.Category;
import vn.devpro.javaweb27.model.User;
import vn.devpro.javaweb27.service.CategoryService;
import vn.devpro.javaweb27.service.UserService;

@Controller
@RequestMapping(value = "/admin/category/")
public class CategoryController extends BaseController implements Jw27Constant{

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(final Model model,
			final HttpServletRequest request) {
		
		SearchModel categorySearch = new SearchModel();
		// Tìm theo status
		categorySearch.setStatus(2); // All
		String status = request.getParameter("status");
		if (!StringUtils.isEmpty(status)) { // Có chọn status
			categorySearch.setStatus(Integer.parseInt(status));
		}

		// Tìm theo keyword
		categorySearch.setKeyword(null);
		String keyword = request.getParameter("keyword");
		if (!StringUtils.isEmpty(keyword)) { // Có chọn category
			categorySearch.setKeyword((keyword));
		}

		// Kiem tra tieu chi tim kiem tu ngay den ngay
		String beginDate = null;
		String endDate = null;
		if (!StringUtils.isEmpty(request.getParameter("beginDate"))
				&& !StringUtils.isEmpty(request.getParameter("endDate"))) {
			beginDate = request.getParameter("beginDate");
			endDate = request.getParameter("endDate");
		}
		categorySearch.setBeginDate(beginDate);
		categorySearch.setEndDate(endDate);
		
		// Bắt đầu phân trang
		if (!StringUtils.isEmpty(request.getParameter("currentPage"))) { // Bấm nút chuyển trang
			categorySearch.setCurrentPage(Integer.parseInt(request.getParameter("currentPage")));
		}
		else {
			categorySearch.setCurrentPage(1);// Lần đầu truy cập luôn hiển thị trang 1
		}
		
		
		List<Category> allProducts = categoryService.searchCategory(categorySearch);
		
		List<Category> categories = new ArrayList<Category>();
		// Tổng số trang theo tìm kiếm
		int totalPages = allProducts.size() / SIZE_OF_PAGE;
		if (allProducts.size() % SIZE_OF_PAGE > 0) {
			totalPages++;
		}
		
		// Nếu tổng số trang < trang hiện tại (lại bấm tìm kiếm)
		if (totalPages < categorySearch.getCurrentPage()) {
			categorySearch.setCurrentPage(1);
		}
		
		// Lấy danh sách sp cần hiển thị trong 1 trang
		int firstIndex = (categorySearch.getCurrentPage() - 1) * SIZE_OF_PAGE; // Vị trí đầu 1 trang
		int index = firstIndex, count = 0;
		while (index < allProducts.size() && count < SIZE_OF_PAGE) {
			categories.add(allProducts.get(index));
			index++;
			count++;
		}
		
		// Phân trang
		categorySearch.setSizeOfPage(SIZE_OF_PAGE); // số bản ghi trên 1 trang
		categorySearch.setTotalItems(allProducts.size()); // Tổng số sản phẩm theo tìm kiếm

//		categories = categoryService.findAll();
		model.addAttribute("categories", categories);

		model.addAttribute("categorySearch", categorySearch);


		return "backend/category-list";
	}

	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String add(final Model model) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

		Category category = new Category();
		category.setCreateDate(new Date());

		model.addAttribute("category", category);

		return "backend/category-add";
	}

	@RequestMapping(value = "add-save", method = RequestMethod.POST)
	public String addSave(final Model model, @ModelAttribute("category") Category category) {

		categoryService.saveOrUpdate(category);

		model.addAttribute("category", category);

		return "redirect:/admin/category/list";
	}

	@RequestMapping(value = "edit/{categoryId}", method = RequestMethod.GET)
	public String edit(final Model model, @PathVariable("categoryId") int categoryId) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

//		Lấy category trong DB bằng id
		Category category = categoryService.getByID(categoryId);
		model.addAttribute("category", category);

		return "backend/category-edit";
	}

	@RequestMapping(value = "edit-save", method = RequestMethod.POST)
	public String editSave(final Model model, @ModelAttribute("category") Category category) {

		categoryService.saveOrUpdate(category);

		return "redirect:/admin/category/list";
	}

//	@RequestMapping(value = "delete/{categoryId}", method = RequestMethod.GET)
//	public String delete(final Model model,
//			@PathVariable("categoryId") int categoryId
//			) {
//		
//		categoryService.deleteCategoryById(categoryId);
//		
//		return "redirect:/admin/category/list";
//	}

	@RequestMapping(value = "delete/{categoryId}", method = RequestMethod.GET)
	public String delete(final Model model, @PathVariable("categoryId") int categoryId) {

//		Lấy category trong DB bằng id
		Category category = categoryService.getByID(categoryId);
		category.setStatus(false);
		categoryService.inactiveCategory(category);

		return "redirect:/admin/category/list";
	}
}
