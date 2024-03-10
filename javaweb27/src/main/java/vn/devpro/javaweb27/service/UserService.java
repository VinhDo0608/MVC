package vn.devpro.javaweb27.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import vn.devpro.javaweb27.model.User;

@Service
public class UserService extends BaseService<User>{

	@Override
	public Class<User> clazz() {
		// TODO Auto-generated method stub
		return User.class;
	}
	
	public List<User> findAllActive() {
		return super.executeNativeSql("select * from tbl_user where status=1");
	}
	
	@Transactional
	public void deleteUserById(int id) {
		super.deleteById(id);
	}
	
	@Transactional
	public void inactiveUser(User user) {
		super.saveOrUpdate(user);
	}

}
