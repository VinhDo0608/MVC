package vn.devpro.javaweb27.configurer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


public class UrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	// Điều hướng đến request
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override // CHuyển đến request thích hợp khi xác thực thành công
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		handle(request, response, authentication);
		clearAuthenticationAttributes(request);
		
	}
	
	// Đưa vào role của user login để xác định request chuyển đến (redirect)
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		String targetUrl = determineTargetUrl(authentication); // Lấy url theo role
		if (response.isCommitted()) {
			return;
		}
		redirectStrategy.sendRedirect(request, response, targetUrl); // Điều hướng target URL
	}
	
	// Lấy role của user và trả về Url (action) tương ứng với authentication
	protected String determineTargetUrl(final Authentication authentication) throws IllegalStateException{
		// TODO Auto-generated method stub
		Map<String, String> roleTargetUrlMap = new HashMap<String, String>();
		// key là role name, value là Url (action)
		roleTargetUrlMap.put("ADMIN", "/admin/home");
		roleTargetUrlMap.put("GUEST", "/index");
		
		final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		for (final GrantedAuthority grantedAuthority : authorities) { // authorities lấy trong class user
			String authorityName = grantedAuthority.getAuthority(); // role name
			
			if (roleTargetUrlMap.containsKey(authorityName)) {
				return roleTargetUrlMap.get(authorityName); // Trả về target url của user đăng nhập
			}
		}
		throw new IllegalStateException();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
	
}
