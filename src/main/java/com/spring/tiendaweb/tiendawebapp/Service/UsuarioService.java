package com.spring.tiendaweb.tiendawebapp.Service;

import com.spring.tiendaweb.tiendawebapp.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.spring.tiendaweb.tiendawebapp.Entity.Usuario;

@Service
public class UsuarioService {

@Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listaUsuarios(){
        return usuarioRepository.findAll();
    }

    public void guardar(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public void eliminar(Long id){
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existePorUsuario(String usuario) {
        return usuarioRepository.existsByUsuario(usuario);
    }
}
