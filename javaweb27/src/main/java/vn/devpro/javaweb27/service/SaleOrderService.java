package vn.devpro.javaweb27.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import vn.devpro.javaweb27.model.SaleOrder;

@Service
public class SaleOrderService extends BaseService<SaleOrder>{

	@Override
	public Class<SaleOrder> clazz() {
		// TODO Auto-generated method stub
		return SaleOrder.class;
	}
	
	@Transactional
	public SaleOrder saveOrder(SaleOrder saleOrder) {
		return super.saveOrUpdate(saleOrder);
	}

	public List<SaleOrder> findAllActive() {
		return super.executeNativeSql("select * from tbl_sale_order where status=1");
	}
	
	

}
