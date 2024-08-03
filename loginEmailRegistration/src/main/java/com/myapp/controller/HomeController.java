package com.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myapp.entity.Employee;
import com.myapp.service.EmployeeService;

@Controller
public class HomeController {

	@Autowired
	EmployeeService service;

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("employee", new Employee());
		return "register";
	}

	@PostMapping("/register")
	public String registerProcess(@ModelAttribute Employee employee, Model model) {
		Employee existingEmployee = service.findEmployeeByEmail(employee.getEmail());
		if (existingEmployee != null) {
			model.addAttribute("message", "Email already exists");
			return "register";
		}
		String siteURL = "http://localhost:8080"; 
		service.registerUser(employee, siteURL);
		model.addAttribute("message", "User registered successfully. Please check your email to verify your account.");
		return "login";
	}

	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("code") String code, Model model) {
		boolean verified = service.verify(code);
		String message = verified ? "Verification successful. You can now login."
				: "Verification failed. Invalid or expired code.";
		model.addAttribute("message", message);
		return "login";
	}

	@GetMapping("/login")
	public String loginPage(Model model) {
		model.addAttribute("employee", new Employee());
		return "login";
	}

	@PostMapping("/login")
	public String loginProcess(@ModelAttribute Employee employee, Model model) {
		Employee existingEmployee = service.findEmployeeByEmail(employee.getEmail());
		if (existingEmployee == null || !existingEmployee.getPassword().equals(employee.getPassword())) {
			model.addAttribute("message", "Invalid email or password");
			return "login";
		}
		if (!existingEmployee.isEnabled()) {
			model.addAttribute("message", "Please verify your account first");
			return "login";
		}
		model.addAttribute("message", "Login successful");
		model.addAttribute("username", existingEmployee.getFirstName());
		return "about";
	}
}
