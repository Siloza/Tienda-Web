// ✅ LoginController.java - SOLO CAMBIAR LÍNEA 32
package com.spring.tiendaweb.tiendawebapp.Controller;

import com.spring.tiendaweb.tiendawebapp.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @GetMapping("/Login")
    public String mostrarLogin(){
        return "Login";
    }

    @PostMapping("/Login")
    public String procesarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {
        
        // Validar que no estén vacíos
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Por favor, complete todos los campos");
            return "Login";
        }

        //validación con CACHE
        if (authService.validarUsuarioConCache(username.trim(), password)) {
            model.addAttribute("success", "¡Inicio de sesión exitoso! Bienvenido " + username);
            model.addAttribute("username", username);
            return "Login"; //  No se me olvide cambiar despues cuando hagamos el inicio
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "Login";
        }
    }
}