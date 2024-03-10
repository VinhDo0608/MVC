package vn.devpro.javaweb27.controller.frontend;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.devpro.javaweb27.controller.BaseController;
import vn.devpro.javaweb27.dto.Contact;
import vn.devpro.javaweb27.dto.Jw27Constant;

@Controller
public class ContactController extends BaseController implements Jw27Constant{

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String contact(final Model model, final HttpServletResponse response, final HttpServletRequest request)
			throws IOException {

		return "frontend/contact/contact";
	}

	@RequestMapping(value = "/contact-send", method = RequestMethod.POST)
	public String contactSend(final Model model, final HttpServletResponse response, final HttpServletRequest request)
			throws IOException {
		Contact contact = new Contact();

		contact.setName(request.getParameter("txtName")); // Lấy theo name của input
		System.out.println(contact.getName());

		contact.setMobile(request.getParameter("txtMobile"));
		System.out.println(contact.getMobile());
		return "frontend/contact/contact";
	}

	@RequestMapping(value = "/contact-edit", method = RequestMethod.GET)
	public String contactEdit(final Model model, final HttpServletResponse response, final HttpServletRequest request)
			throws IOException {
		Contact contact = new Contact("Phạm Đức", "dp@gmail.com", "09711", "Hà Nội", "Hello World");
		model.addAttribute("contact", contact); // attributeName tên biến trong jsp
												// attributeValue đối tượng hiển thị
		return "frontend/contact/contact-edit";
	}

	@RequestMapping(value = "/contact-edit-save", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> contactEditSave(final Model model, final HttpServletRequest request,
			final HttpServletResponse response, @RequestBody Contact contact) throws IOException {
		System.out.println(contact.getName());
		
		// Sau khi lưu dữ liệu vào DB
				Map<String, Object> jsonResult = new HashMap<String, Object>(); // Gửi lại view
				jsonResult.put("code", 200);
				jsonResult.put("message", "Cảm ơn " + contact.getName() + " đã sử dụng thông tin phản hồi");
		return ResponseEntity.ok(jsonResult);
	}
	
	@RequestMapping(value = "/contact-sf", method = RequestMethod.GET)
	public String contactSf(final Model model, final HttpServletRequest request, HttpServletResponse response) throws IOException {
		model.addAttribute("contact", new Contact());
		return "frontend/contact/contact-sf";
		
	}
	
	@RequestMapping(value = "/contact-sf-save", method = RequestMethod.POST)
	public String contactSfSave(final Model model, final HttpServletRequest request, final HttpServletResponse response,
			@ModelAttribute("contact") Contact contact,   // name{
			@RequestParam("contactFile") MultipartFile contactFile) // Lấy dữ liệu từ spring form
	
		throws IOException{	
		
//		Lưu ảnh vào thư mục, lưu đường dẫn vào DB
		
//		Kiểm tra người dùng có upload file không
		if(contactFile != null && !contactFile.getOriginalFilename().isEmpty()) { // có load file
			String path = FOLDER_UPLOAD + "Contacts/" + contactFile.getOriginalFilename();
			File file = new File(path);
			contactFile.transferTo(file);
		}
		return "frontend/contact/contact-sf";
	}
	
	@RequestMapping(value = "/contact-sf-edit", method = RequestMethod.GET)
	public String contactSfEdit(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {

		Contact contact = new Contact("Phạm Đức", "dp@gmail.com", "0971142403", "Hà Nội", "Hello World");
		model.addAttribute("contact", contact);
		return "frontend/contact/contact-sf-edit";
	}
	
	
}
