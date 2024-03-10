package vn.devpro.javaweb27.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.devpro.javaweb27.model.User;

@Service
public class UserDeltailsServiceImpl extends BaseService<User> implements UserDetailsService{
	
	@Override
	public Class<User> clazz() {
		// TODO Auto-generated method stub
		return User.class;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		// Lấy user trong tbl_user theo username (username từ form login)
		String sql = "select * from tbl_user u where u.username='" + username + "' and u.status=1";
		User user = this.getEntityByNativeSQL(sql);
		if (user != null) {
//			System.out.println("username: " + user.getUsername());
//			System.out.println("password: " + user.getPassword());
			return user;
		}
		
		else return new User(); // UserDetails không được null
	}
	

}
