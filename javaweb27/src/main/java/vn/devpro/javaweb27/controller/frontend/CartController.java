package vn.devpro.javaweb27.controller.frontend;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.devpro.javaweb27.controller.BaseController;
import vn.devpro.javaweb27.dto.Cart;
import vn.devpro.javaweb27.dto.Customer;
import vn.devpro.javaweb27.dto.Jw27Constant;
import vn.devpro.javaweb27.dto.ProductCart;
import vn.devpro.javaweb27.model.Product;
import vn.devpro.javaweb27.model.SaleOrder;
import vn.devpro.javaweb27.model.SaleOrderProduct;
import vn.devpro.javaweb27.model.User;
import vn.devpro.javaweb27.service.ProductService;
import vn.devpro.javaweb27.service.SaleOrderService;

@Controller
public class CartController extends BaseController implements Jw27Constant{
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private SaleOrderService saleOrderService;
	
	// Thêm 1 sản phẩm vào giỏ hàng
	@RequestMapping(value = "/add-to-cart", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addToCart(final Model model,
			final HttpServletRequest request,
			@RequestBody ProductCart addProduct) throws IOException {
		
		HttpSession session = request.getSession();
		Cart cart = null;
		
		// Lấy giỏ hàng trong session
		// + Kiểm tra giỏ hàng đã được tạo trong session chưa?
		if (session.getAttribute("cart") != null) { // Đã có giỏ hàng
			cart = (Cart)session.getAttribute("cart"); // Lấy giỏ hàng
		}
		else { // Chưa tạo giỏ hàng
			cart = new Cart();
			session.setAttribute("cart", cart);
		}
		
		// Lấy sản phẩm trong cơ sở dữ liệu
		Product dbProduct = productService.getByID(addProduct.getProductId());
		
		// Kiểm tra sản phẩm đặt mua đã có trong giỏ hàng chưa
		int index = cart.findProductById(dbProduct.getId());
		if (index != -1) { // Sản phẩm đã có trong giỏ hàng
			cart.getProductCarts().get(index).setQuantity(cart.getProductCarts().get(index).getQuantity().add(addProduct.getQuantity()));
		}
		else {
			addProduct.setProductName(dbProduct.getName());
			addProduct.setAvatar(dbProduct.getAvatar());
			addProduct.setPrice(dbProduct.getPrice());
			
			cart.getProductCarts().add(addProduct); // Thêm sản phẩm vào giỏ hàng
		}
		
		// Thiết lặp lại giỏ hàng trong session
		session.setAttribute("cart", cart);
		
		// Trả về dữ liệu cho view
		Map<String, Object> jsonResult = new HashMap<String, Object>();
		jsonResult.put("code", 200);
		jsonResult.put("totalCartProducts", cart.totalCartProduct());
		jsonResult.put("message", "Đã thêm sản phẩm " + addProduct.getProductName() +" vào giỏ hàng!");
		
		return ResponseEntity.ok(jsonResult);
	}
	
	// Hiển thị giỏ hàng
	@RequestMapping(value = "/cart-view", method = RequestMethod.GET)
	public String cartView(final Model model, final HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession();
		if (session.getAttribute("cart") != null) {
			Cart cart = (Cart)session.getAttribute("cart");
			model.addAttribute("totalCartPrice",cart.totalCartPrice());
			String message = "Có tổng cộng " + cart.totalCartProduct() + " sản phẩm trong giỏ hàng!";
			model.addAttribute("message",message);
			
		}
		else {
			String errorMessage = "Không có sản phẩm nào trong giỏ hàng!";
			model.addAttribute("errorMessage",errorMessage);
		}
		return "frontend/cart-view";

	}
	
	// Thêm bớt sản phẩm trong giỏ hàng
	@RequestMapping(value = "/update-product-quantity", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updateProductQuantity(
			@RequestBody ProductCart productCart,
			final HttpServletRequest request
			) throws IOException {
		Map<String, Object> jsonResult = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("cart") != null) {
			Cart cart = (Cart)session.getAttribute("cart");
			// Cập nhật số lượng
			int index = cart.findProductById(productCart.getProductId());
			BigInteger oldQuantity = cart.getProductCarts().get(index).getQuantity();
			BigInteger newQuantity = oldQuantity.add(productCart.getQuantity()); // +1 // -1
			if (newQuantity.intValue() < 1) {
				newQuantity = BigInteger.ONE;
			}
			cart.getProductCarts().get(index).setQuantity(newQuantity);
			jsonResult.put("newQuantity", newQuantity);
		}
		jsonResult.put("productId", productCart.getProductId());
		return ResponseEntity.ok(jsonResult);
	}
	
	// Xác nhận đặt hàng
		@RequestMapping(value = "/place-order", method = RequestMethod.POST)
		public ResponseEntity<Map<String, Object>> placeOrder(
				@RequestBody Customer customer,
				final HttpServletRequest request
				) throws IOException {
			Map<String, Object> jsonResult = new HashMap<String, Object>();
			// Kiểm tra thông tin customer bắt buộc
			if (StringUtils.isEmpty(customer.getTxtName())) {
				jsonResult.put("message", "Bạn chưa nhập họ tên");
			}
			else if (StringUtils.isEmpty(customer.getTxtMobile())) {
				jsonResult.put("message", "Bạn chưa nhập số điện thoại");
			}
			else {
				HttpSession session = request.getSession();
				if (session.getAttribute("cart") == null) {
					jsonResult.put("message", "Bạn chưa có giỏ hàng");
				}
				else {
					Cart cart = (Cart)session.getAttribute("cart");
					// Lưu các sản phẩm trong giỏ hàng vào tbl_sale_order_product
					SaleOrder saleOrder = new SaleOrder();
					for (ProductCart productCart : cart.getProductCarts()) {
						SaleOrderProduct saleOrderProduct = new SaleOrderProduct();
						// Lấy sản phẩm trong db
						Product dbproduct = productService.getByID(productCart.getProductId());
						saleOrderProduct.setProduct(dbproduct);
						saleOrderProduct.setQuantity(productCart.getQuantity().intValue());
						
						saleOrder.addRelationalSaleOrderProduct(saleOrderProduct);
					}
					// Lưu đơn hàng vào tbl_sale_order
					Calendar cal = Calendar.getInstance();
					String code = customer.getTxtMobile() + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH);
					saleOrder.setCode(code);
					User user = new User();
					user.setId(1);
					saleOrder.setUser(user);
					
					saleOrder.setCustomerName(customer.getTxtName());
					saleOrder.setCustomerMobile(customer.getTxtMobile());
					saleOrder.setCustomerEmail(customer.getTxtEmail());
					saleOrder.setCustomerAddress(customer.getTxtAddress());
					saleOrder.setTotal(cart.totalCartPrice());
					
					saleOrderService.saveOrder(saleOrder);
					
					jsonResult.put("message", "Đặt hàng thành công!");
					
					// Xóa giỏ hàng sau khi đã đặt hàng
					cart = new Cart();
					session.setAttribute("cart", cart);
				}
					
			}
			
			return ResponseEntity.ok(jsonResult);
		}
}
