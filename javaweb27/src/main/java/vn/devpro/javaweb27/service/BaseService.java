package vn.devpro.javaweb27.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import vn.devpro.javaweb27.model.BaseModel;

@Service
public abstract class BaseService<E extends BaseModel> {
	
	@PersistenceContext
	EntityManager entityManager;

	public abstract Class<E> clazz();

//	Lấy 1 bản ghi theo id
	public E getByID(int id) {
		return entityManager.find(clazz(), id);
	}

//	Lấy tất cả các bản ghi trong một table
	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		Table table = clazz().getAnnotation(Table.class);
		return (List<E>) entityManager.createNativeQuery("select * from " + table.name(),clazz()).getResultList();
	}
	
	
//	Thêm hoặc sửa một bản ghi
	@Transactional
	public E saveOrUpdate(E entity) {
		if(entity.getId() == null || entity.getId() <= 0) { // Add new entity
			entityManager.persist(entity);
			return entity;
		} else { // update entity
			return entityManager.merge(entity);
		}
	}
	
//	Xóa một bản ghi theo entity
	public void delete(E entity) {
		entityManager.remove(entity);
	}
	
//	Delete theo id
	public void deleteById(int id) {
		E entity = this.getByID(id);
		delete(entity);
	}
	
	@SuppressWarnings("unchecked")
	public List<E> executeNativeSql(String sql) {
		try {
			Query query = entityManager.createNativeQuery(sql, clazz());
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Get entity
	public E getEntityByNativeSQL(String sql) {
		List<E> list = executeNativeSql(sql);
		if (list.size() > 0) {
			return list.get(0);
		}
		else {
			return null;
		}
	}
}
