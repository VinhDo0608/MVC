package vn.devpro.javaweb27.controller.backend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.devpro.javaweb27.dto.Jw27Constant;
import vn.devpro.javaweb27.model.BaseModel;
import vn.devpro.javaweb27.model.SaleOrder;
import vn.devpro.javaweb27.service.SaleOrderService;

@Controller
@RequestMapping(value = "/admin/order/")
public class SaleOrderController  extends BaseModel implements Jw27Constant{
	
	@Autowired
	private SaleOrderService saleOrderService;
	
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(final Model model) {
		
		List<SaleOrder> saleOrders = saleOrderService.findAllActive();
		model.addAttribute("saleOrders", saleOrders);
		
		return "backend/order-list";
	}
}
