package vn.devpro.javaweb27.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import vn.devpro.javaweb27.model.Role;

@Service
public class RoleService extends BaseService<Role> {

	@Override
	public Class<Role> clazz() {
		// TODO Auto-generated method stub
		return Role.class;
	}
	
	public List<Role> findAllActive() {
		return super.executeNativeSql("select * from tbl_role where status=1");
	}
	
	@Transactional
	public void deleteRoleById(int id) {
		super.deleteById(id);
	}
	
	@Transactional
	public void inactiveCategory(Role role) {
		super.saveOrUpdate(role);
	}

	public Role getRoleByName(String string) {
		// TODO Auto-generated method stub
		
		String sql = "select * from tbl_role where name='" + string + "'";
		List<Role> roles = super.executeNativeSql(sql);
		
		if (roles.size() > 0) {
			return roles.get(0);
		}
		else {
			return new Role();
		}
	}
	

}
