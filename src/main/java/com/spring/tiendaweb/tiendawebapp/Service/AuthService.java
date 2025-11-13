package com.spring.tiendaweb.tiendawebapp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.spring.tiendaweb.tiendawebapp.Entity.Usuario;

@Service
public class AuthService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private UsuarioService usuarioService;  // ← AHORA SÍ UsuarioService

    public boolean validarUsuarioConCache(String usuario, String contraseña) {
        System.out.println("🔍 Buscando usuario en cache (Redis): " + usuario);
        
        String passwordEnCache = redisTemplate.opsForValue().get("user:" + usuario);
        
        if (passwordEnCache != null) {
            if (passwordEnCache.equals(contraseña)) {
                System.out.println("✅ Login exitoso desde CACHE: " + usuario);
                return true;
            } else {
                System.out.println("❌ Contraseña incorrecta en CACHE: " + usuario);
                return false;
            }
        }
        
        System.out.println("🔍 Usuario no encontrado en cache, buscando en MySQL: " + usuario);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsuario(usuario);  // ← usuarioService
        
        if (usuarioOpt.isPresent()) {
            Usuario usuarioEntity = usuarioOpt.get();
            if (usuarioEntity.getContraseña().equals(contraseña)) {
                redisTemplate.opsForValue().set("user:" + usuario, contraseña);
                System.out.println("✅ Login exitoso desde MySQL, guardado en CACHE: " + usuario);
                return true;
            } else {
                System.out.println("❌ Contraseña incorrecta en MySQL: " + usuario);
                return false;
            }
        }
        
        System.out.println("❌ Usuario no existe ni en CACHE ni en MySQL: " + usuario);
        return false;
    }

    public void guardarUsuario(String username, String password) {
        redisTemplate.opsForValue().set("user:" + username, password);
        System.out.println("✅ Usuario guardado en Redis: " + username);
    }

    public boolean existeUsuario(String username) {
        return redisTemplate.hasKey("user:" + username);
    }

    public void eliminarUsuario(String username) {
        redisTemplate.delete("user:" + username);
    }
}