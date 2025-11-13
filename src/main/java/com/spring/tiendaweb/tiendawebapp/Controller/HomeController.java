package com.spring.tiendaweb.tiendawebapp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String mostrarHome() {
        return "Home";
    }

    @GetMapping("/home")
    public String mostrarHomePage() {
        return "Home";
    }
}