// ✅ LoginController.java - SOLO CAMBIAR LÍNEA 32
package com.spring.tiendaweb.tiendawebapp.Controller;

import com.spring.tiendaweb.tiendawebapp.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.tiendaweb.tiendawebapp.Entity.Usuario;
import com.spring.tiendaweb.tiendawebapp.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/Login")
    public String mostrarLogin(){
        return "Login";
    }

    @PostMapping("/Login")
    public String procesarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model,
            HttpSession session) {
        
        // Validar que no estén vacíos
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Por favor, complete todos los campos");
            return "Login";
        }

        //validación con CACHE (Redis)
        if (authService.validarUsuarioConCache(username.trim(), password)) {
            // ✅ VERIFICAR/CREAR USUARIO EN MYSQL
            java.util.Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsuario(username.trim());
            
            if (usuarioOpt.isEmpty()) {
                // Si no existe en MySQL, crearlo
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setUsuario(username.trim());
                nuevoUsuario.setContraseña(password); // en producción, encriptar
                nuevoUsuario.setPrimernombre(username.trim());
                nuevoUsuario.setSegundonombre("");
                nuevoUsuario.setEmail(username.trim() + "@fitcrows.local");
                usuarioService.guardar(nuevoUsuario);
                System.out.println("✅ Usuario creado en MySQL: " + username.trim());
            }
            
            // ✅ GUARDAR USUARIO EN SESIÓN
            session.setAttribute("usuarioAutenticado", username.trim());
            System.out.println("✅ Usuario " + username.trim() + " autenticado. Sesión: " + session.getId());
            model.addAttribute("username", username);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "Login";
        }
    }

    @GetMapping("/Logout")
    public String logout(HttpSession session) {
        // ✅ LIMPIAR SESIÓN
        session.invalidate();
        System.out.println("✅ Sesión cerrada correctamente");
        return "redirect:/home";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registrar(@RequestBody RegistroRequest req) {
        if (req.usuario() == null || req.usuario().isBlank() || req.password() == null || req.password().isBlank()
                || req.email() == null || req.email().isBlank() || req.nombre() == null || req.nombre().isBlank()) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
        }

        if (usuarioService.existePorUsuario(req.usuario())) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }
        if (usuarioService.existePorEmail(req.email())) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }

        Usuario u = new Usuario();
        u.setPrimernombre(req.nombre());
        u.setSegundonombre(req.segundoNombre() != null ? req.segundoNombre() : "");
        u.setEmail(req.email());
        u.setUsuario(req.usuario());
        u.setContraseña(req.password());
        usuarioService.guardar(u);

        // cache en redis (no romper si Redis no está disponible)
        try {
            authService.guardarUsuario(req.usuario(), req.password());
        } catch (Exception e) {
            // log rápido y continuar para no devolver 500 al usuario
            System.err.println("No se pudo guardar en Redis: " + e.getMessage());
        }

        return ResponseEntity.ok("Registro exitoso");
    }

    public record RegistroRequest(String nombre, String segundoNombre, String email, String usuario, String password) {}
}