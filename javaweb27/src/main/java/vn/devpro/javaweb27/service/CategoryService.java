package vn.devpro.javaweb27.service;



import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.devpro.javaweb27.dto.SearchModel;
import vn.devpro.javaweb27.model.Category;

@Service
public class CategoryService extends BaseService<Category>{

	@Override
	public Class<Category> clazz() {
		// TODO Auto-generated method stub
		return Category.class;
	}
	
	public List<Category> findAllActive() {
		return super.executeNativeSql("select * from tbl_category where status=1");
	}
	
	@Transactional
	public void deleteCategoryById(int id) {
		super.deleteById(id);
	}
	
	@Transactional
	public void inactiveCategory(Category category) {
		super.saveOrUpdate(category);
	}
	
	public List<Category> searchCategory(SearchModel categorySearch) {
		// Tạo câu lệnh sql
		String sql = "SELECT * FROM tbl_category p WHERE 1=1";

		// Tìm kiếm với tiêu chí status
		if (categorySearch.getStatus() != 2) { // Có chọn Active/ Inactive
			sql += " AND p.status=" + categorySearch.getStatus();
		}

		// Tìm kiếm với tiêu chí keyword
		if (!StringUtils.isEmpty(categorySearch.getKeyword())) { // Có chọn Active/ Inactive

			String keyword = categorySearch.getKeyword().toLowerCase();

			sql += " AND (LOWER(p.name) like '%" + keyword + "%' " + " OR LOWER(p.short_description) like '%" + keyword
					+ "%' " + " OR LOWER(p.seo) like '%" + keyword + "%' )";
		}

//		Tìm kiếm theo ngày tháng
		if (!StringUtils.isEmpty(categorySearch.getBeginDate()) && !StringUtils.isEmpty(categorySearch.getEndDate())) {
			String beginDate = categorySearch.getBeginDate();
			String endDate = categorySearch.getEndDate();

			sql += " and p.create_date between '" + beginDate + "' and '" + endDate + "'";
		}
		return super.executeNativeSql(sql);
	}

}
