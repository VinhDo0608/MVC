package vn.devpro.javaweb27.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_product_image")
public class ProductImage extends BaseModel {
	@Column(name = "title", length = 500, nullable = true)
	private String tile;

	@Column(name = "path", length = 300, nullable = true)
	private String path;

//	Mapping many-to-one: product_image-to-product
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Product product;

	public ProductImage() {
		super();
	}

	public ProductImage(Integer id, Date createDate, Date updateDate, Boolean status, String tile, String path,
			Product product) {
		super(id, createDate, updateDate, status);
		this.tile = tile;
		this.path = path;
		this.product = product;
	}

	public String getTile() {
		return tile;
	}

	public void setTile(String tile) {
		this.tile = tile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
