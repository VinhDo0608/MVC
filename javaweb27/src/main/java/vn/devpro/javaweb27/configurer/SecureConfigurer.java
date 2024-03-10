package vn.devpro.javaweb27.configurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecureConfigurer extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception{
		
		// Bắt đầu cấu hình
		http.csrf().disable().authorizeRequests() // Bắt các request từ browser
		
		// Cho phép các request, static resource không bị ràng buộc login
		.antMatchers("/frontend/**", "/backend/**", "/FileUploads/**", "/login/**", "/logout/**").permitAll()
		
		// Các request kiểu "/admin/** phải login (xác thực)
//		.antMatchers("/admin/**").authenticated() // step 1 + @
		
		// Các request kiểu "/admin/** phải có role là ADMIN // step 3
		.antMatchers("/admin/**").hasAnyAuthority("ADMIN")
		
		.and()
		
		// Nếu chưa login (xác thực) thì redirect đến trang login
		// Với "/login" là 1 request (trong LoginController)
		.formLogin().loginPage("/login").loginProcessingUrl("/login_processing_url")
		
//		.defaultSuccessUrl("/admin/home", true) // login thành công vào trang home
		
		.successHandler(new UrlAuthenticationSuccessHandler()) // login thành công: chuyển đến request phù hợp với step 3
		
		// Login không thành công
		.failureUrl("/login?login_error=true")
		
		.and()
		
		// Cấu hình logout
		.logout().logoutUrl("/logout").logoutSuccessUrl("/index")
			.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		
		.and()
		.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400);
		
	}

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder(4));
	}
	
//	public static void main(String[] args) {
//		System.out.println(new BCryptPasswordEncoder(4).encode("123"));
//	}
}
