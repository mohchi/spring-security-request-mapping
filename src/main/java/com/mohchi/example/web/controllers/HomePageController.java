package com.mohchi.example.web.controllers;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomePageController {

	@RequestMapping
	@PreAuthorize("isAuthenticated()")
	public String authenticatedHomePage(Principal principal, ModelMap model) {
		model.addAttribute("name", principal.getName());
		return "authenticatedHomePage";
	}

	@RequestMapping
	public String homePage() {
		return "homePage";
	}

	@RequestMapping("/secure/{name}")
	@PreAuthorize("authentication.name == #name")
	public String securePage(Principal principal, ModelMap model) {
		model.addAttribute("name", principal.getName());
		return "securePage";
	}

}
