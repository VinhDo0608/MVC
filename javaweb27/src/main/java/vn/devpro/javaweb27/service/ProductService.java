package vn.devpro.javaweb27.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.aspectj.apache.bcel.classfile.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import vn.devpro.javaweb27.dto.Jw27Constant;
import vn.devpro.javaweb27.dto.SearchModel;
import vn.devpro.javaweb27.model.Category;
import vn.devpro.javaweb27.model.Product;
import vn.devpro.javaweb27.model.ProductImage;

@Service
public class ProductService extends BaseService<Product> implements Jw27Constant {

	@Override
	public Class<Product> clazz() {
		// TODO Auto-generated method stub
		return Product.class;
	}

	public List<Product> findAllActive() {
		return super.executeNativeSql("select * from tbl_product where status=1");
	}

//	Phương thức kiểm tra 1 file có được upload hay không ?
	public boolean isUploadFile(MultipartFile file) {
		if (file == null || file.getOriginalFilename().isEmpty())
			return false;
		else
			return true;
	}

//	Phương thức kiểm tra danh sách file có upload file nào không
	public boolean isUploadFiles(MultipartFile[] files) {
		if (files == null || files.length == 0)
			return false;
		else
			return true;
	}

//	Save new product to database
	@Transactional
	public Product saveAddProduct(Product product, MultipartFile avatarFile, MultipartFile[] imageFiles)
			throws IOException {

//		Lưu avatar file
		if (isUploadFile(avatarFile)) { // Có upload file avatar
//			Lưu file vào thư mục Product/Avatar
			String path = FOLDER_UPLOAD + "Product/Avatar/" + avatarFile.getOriginalFilename();
			File file = new File(path);
			avatarFile.transferTo(file);
//			Lưu đường dẫn vào bảng tbl_product
			product.setAvatar("Product/Avatar/" + avatarFile.getOriginalFilename());

		}

//		Lưu images file
		if (isUploadFiles(imageFiles)) { // Co upload danh sach anh
			for (MultipartFile imageFile : imageFiles) {
				if (isUploadFile(imageFile)) { // File co upload

//					Luu file vao thu muc Product/Image?
					String path = FOLDER_UPLOAD + "Product/Image/" + imageFile.getOriginalFilename();
					File file = new File(path);
					imageFile.transferTo(file);

//					Luu duong dan vao tbl_product_image
					ProductImage productImage = new ProductImage();
					productImage.setTile(imageFile.getOriginalFilename());
					productImage.setPath("Product/Image/" + imageFile.getOriginalFilename());
					productImage.setStatus(Boolean.TRUE);
					productImage.setCreateDate(new Date());

//					Luu duong dan anh sang bang tbl_product_image
					product.addRelationalProductImage(productImage);

				}
			}

		}
		return super.saveOrUpdate(product);
	}

	@Transactional
	public Product saveEditProduct(Product product, MultipartFile avatarFile, MultipartFile[] imageFiles)
			throws IOException {
//		Lấy product trong db
		Product dbProduct = super.getByID(product.getId());

//		Lưu avatar file
		if (isUploadFile(avatarFile)) { // Có upload file avatar
//			Xóa avatar cũ(xóa file avatar)
			String path = FOLDER_UPLOAD + "Product/Avatar/" + dbProduct.getAvatar();
			File file = new File(path);
			file.delete();

//			Lưu file avatar mới vào thư mục Product/Avatar
			path = FOLDER_UPLOAD + "Product/Image/" + avatarFile.getOriginalFilename();
			file = new File(path);
			avatarFile.transferTo(file);
			// Lưu đường dẫn của avatar mới vào bảng tbl_product
			product.setAvatar("Product/Avatar/" + avatarFile.getOriginalFilename());

		} else { // Người dùng không upload avatar file
					// Giữ nguyên avatar cũ
			product.setAvatar(dbProduct.getAvatar());
		}

//		Lưu images file
		if (isUploadFiles(imageFiles)) { // Co upload danh sach anh
			for (MultipartFile imageFile : imageFiles) {
				if (isUploadFile(imageFile)) { // File co upload

//					Luu file vao thu muc Product/Image?
					String path = FOLDER_UPLOAD + "Product/Image/" + imageFile.getOriginalFilename();
					File file = new File(path);
					imageFile.transferTo(file);

//					Luu duong dan vao tbl_product_image
					ProductImage productImage = new ProductImage();
					productImage.setTile(imageFile.getOriginalFilename());
					productImage.setPath("Product/Image/" + imageFile.getOriginalFilename());
					productImage.setStatus(Boolean.TRUE);
					productImage.setCreateDate(new Date());

//					Luu duong dan anh sang bang tbl_product_image
					product.addRelationalProductImage(productImage);

				}
			}

		}
		return super.saveOrUpdate(product);
	}

	@Autowired
	private ProductImageService productImageService;

	@Transactional
	public void deleteProduct(Product product) {
//		Lấy danh sách ảnh của product trong tbl_product_image
		String sql = "select * from tbl_product_image where product_id=" + product.getId();
		List<ProductImage> productImages = productImageService.executeNativeSql(sql);

		for (ProductImage productImage : productImages) {
//			Xóa file trong thư mục Product trong Product/Image và
//			Xóa lần lượt các đường dẫn ảnh trong tbl+product_image

//			Xóa file trong thư mục Product image (truoc)
			String path = FOLDER_UPLOAD + productImage.getPath();
			File file = new File(path);
			file.delete();
//			Xóa bản ghi thông tin ảnh trong tbl_product_image
//			product.removeRelationalProductImage(productImage);

		}

//		Xóa file avatar trong thư mục Product/Avatar
		String path = FOLDER_UPLOAD + product.getAvatar();
		File file = new File(path);
		file.delete();

//		Xóa product trong database
		super.delete(product);
	}

	public List<Product> searchProduct(SearchModel productSearch) {
		// Tạo câu lệnh sql
		String sql = "SELECT * FROM tbl_product p WHERE 1=1";

		// Tìm kiếm với tiêu chí status
		if (productSearch.getStatus() != 2) { // Có chọn Active/ Inactive
			sql += " AND p.status=" + productSearch.getStatus();
		}

		// Tìm kiếm với tiêu chí category
		if (productSearch.getCategoryId() != 0) { // Có chọn Active/ Inactive
			sql += " AND p.category_id=" + productSearch.getCategoryId();
		}

		// Tìm kiếm với tiêu chí keyword
		if (!StringUtils.isEmpty(productSearch.getKeyword())) { // Có chọn Active/ Inactive

			String keyword = productSearch.getKeyword().toLowerCase();

			sql += " AND (LOWER(p.name) like '%" + keyword + "%' " + " OR LOWER(p.short_description) like '%" + keyword
					+ "%' " + " OR LOWER(p.seo) like '%" + keyword + "%' )";
		}

//		Tìm kiếm theo ngày tháng
		if (!StringUtils.isEmpty(productSearch.getBeginDate()) && !StringUtils.isEmpty(productSearch.getEndDate())) {
			String beginDate = productSearch.getBeginDate();
			String endDate = productSearch.getEndDate();

			sql += " and p.create_date between '" + beginDate + "' and '" + endDate + "'";
		}
		return super.executeNativeSql(sql);
	}

}
