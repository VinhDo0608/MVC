package vn.devpro.javaweb27.controller.backend;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.devpro.javaweb27.dto.Jw27Constant;
import vn.devpro.javaweb27.dto.SearchModel;
import vn.devpro.javaweb27.model.BaseModel;
import vn.devpro.javaweb27.model.Category;
import vn.devpro.javaweb27.model.Product;
import vn.devpro.javaweb27.model.User;
import vn.devpro.javaweb27.service.CategoryService;
import vn.devpro.javaweb27.service.ProductService;
import vn.devpro.javaweb27.service.UserService;

@Controller
@RequestMapping(value = "/admin/product/")
public class ProductController extends BaseModel implements Jw27Constant{
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

//	@RequestMapping(value = "list", method = RequestMethod.GET)
//	public String list(final Model model) {
//		
////		List<Product> products = productService.findAll();
//		List<Product> products = productService.findAllActive();
//		model.addAttribute("products", products);
//		
//		return "backend/product-list";
//	}

	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(final Model model, final HttpServletRequest request) {

		SearchModel productSearch = new SearchModel();
		// Tìm theo status
		productSearch.setStatus(2); // All
		String status = request.getParameter("status");
		if (!StringUtils.isEmpty(status)) { // Có chọn status
			productSearch.setStatus(Integer.parseInt(status));
		}

		// Tìm theo category
		productSearch.setCategoryId(0); // All
		String categoryId = request.getParameter("categoryId");
		if (!StringUtils.isEmpty(categoryId)) { // Có chọn category
			productSearch.setCategoryId(Integer.parseInt(categoryId));
		}

		// Tìm theo keyword
		productSearch.setKeyword(null);
		String keyword = request.getParameter("keyword");
		if (!StringUtils.isEmpty(keyword)) { // Có chọn category
			productSearch.setKeyword((keyword));
		}

		// Kiem tra tieu chi tim kiem tu ngay den ngay
		String beginDate = null;
		String endDate = null;
		if (!StringUtils.isEmpty(request.getParameter("beginDate"))
				&& !StringUtils.isEmpty(request.getParameter("endDate"))) {
			beginDate = request.getParameter("beginDate");
			endDate = request.getParameter("endDate");
		}
		productSearch.setBeginDate(beginDate);
		productSearch.setEndDate(endDate);
		
		// Bắt đầu phân trang
		if (!StringUtils.isEmpty(request.getParameter("currentPage"))) { // Bấm nút chuyển trang
			productSearch.setCurrentPage(Integer.parseInt(request.getParameter("currentPage")));
		}
		else {
			productSearch.setCurrentPage(1);// Lần đầu truy cập luôn hiển thị trang 1
		}
		
		
		List<Product> allProducts = productService.searchProduct(productSearch);
		
		List<Product> products = new ArrayList<Product>();
		// Tổng số trang theo tìm kiếm
		int totalPages = allProducts.size() / SIZE_OF_PAGE;
		if (allProducts.size() % SIZE_OF_PAGE > 0) {
			totalPages++;
		}
		
		// Nếu tổng số trang < trang hiện tại (lại bấm tìm kiếm)
		if (totalPages < productSearch.getCurrentPage()) {
			productSearch.setCurrentPage(1);
		}
		
		// Lấy danh sách sp cần hiển thị trong 1 trang
		int firstIndex = (productSearch.getCurrentPage() - 1) * SIZE_OF_PAGE; // Vị trí đầu 1 trang
		int index = firstIndex, count = 0;
		while (index < allProducts.size() && count < SIZE_OF_PAGE) {
			products.add(allProducts.get(index));
			index++;
			count++;
		}
		
		// Phân trang
		productSearch.setSizeOfPage(SIZE_OF_PAGE); // số bản ghi trên 1 trang
		productSearch.setTotalItems(allProducts.size()); // Tổng số sản phẩm theo tìm kiếm

		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);

//		List<Product> products = productService.searchProduct(productSearch);
		model.addAttribute("products", products);
		model.addAttribute("productSearch", productSearch);

		return "backend/product-list";
	}

	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String add(final Model model) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

		List<Category> categories = categoryService.findAllActive();
		model.addAttribute("categories", categories);

		Product product = new Product();
		product.setCreateDate(new Date());

		model.addAttribute("product", product);

		return "backend/product-add";
	}

	@RequestMapping(value = "/add-save", method = RequestMethod.POST)
	public String productAddSave(final Model model, @ModelAttribute("product") Product product,
			@RequestParam("avatarFile") MultipartFile avatarFile,
			@RequestParam("imageFiles") MultipartFile[] imageFiles) throws IOException {
//		Lấy danh sách product từ tbl_product trong DB
		productService.saveAddProduct(product, avatarFile, imageFiles);

		return "redirect:/admin/product/add";
	}

	@RequestMapping(value = "edit/{productId}", method = RequestMethod.GET)
	public String edit(final Model model, @PathVariable("productId") int productId) {

		List<User> users = userService.findAll();
		model.addAttribute("users", users);

		List<Category> categories = categoryService.findAllActive();
		model.addAttribute("categories", categories);

		// Lấy product trong db

		Product product = productService.getByID(productId);
		product.setUpdateDate(new Date());

		model.addAttribute("product", product);

		return "backend/product-edit";
	}

	@RequestMapping(value = "/edit-save", method = RequestMethod.POST)
	public String productEditSave(final Model model, @ModelAttribute("product") Product product,
			@RequestParam("avatarFile") MultipartFile avatarFile,
			@RequestParam("imageFiles") MultipartFile[] imageFiles) throws IOException {
//		Lấy danh sách product từ tbl_product trong DB
		productService.saveEditProduct(product, avatarFile, imageFiles);

		return "redirect:/admin/product/list";
	}

//	@RequestMapping(value = "delete/{productId}", method = RequestMethod.GET)
//	public String delete(final Model model,
//			@PathVariable("productId") int productId) {
//		
//		//Lấy product trong db
//		
//		Product product = productService.getByID(productId);
//		
//		productService.deleteProduct(product);
//		
//		return "redirect:/admin/product/list";
//	}

	@RequestMapping(value = "delete/{productId}", method = RequestMethod.GET)
	public String delete(final Model model, @PathVariable("productId") int productId) {

		// Lấy product trong db

		Product product = productService.getByID(productId);

		product.setStatus(false);
		productService.saveOrUpdate(product);

		return "redirect:/admin/product/list";
	}

}
