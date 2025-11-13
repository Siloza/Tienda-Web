package com.spring.tiendaweb.tiendawebapp.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.List;


@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long usuario_id;

    private String primernombre;
    private String segundonombre;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String usuario;
    @Column(unique = true, nullable = false)
    private String contraseña;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Productos> productos;

    @OneToMany(mappedBy = "usuario")
    private List<Orden> ordenes;

    public Usuario() {}

    public Usuario(String primernombre, String segundonombre, String email, String usuario, String contraseña) {
        this.primernombre = primernombre;
        this.segundonombre = segundonombre;
        this.email = email;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public long getusuario_id() {
        return usuario_id;
    }

    public void setusuario_id(long usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getPrimernombre() {
        return primernombre;
    }

    public void setPrimernombre(String primernombre) {
        this.primernombre = primernombre;
    }

    public String getSegundonombre() {
        return segundonombre;
    }

    public void setSegundonombre(String segundonombre) {
        this.segundonombre = segundonombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    

    public List<Productos> getProductos() {
        return productos;
    }

    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "cliente_id=" + usuario_id +
                ", primernombre='" + primernombre + '\'' +
                ", segundonombre='" + segundonombre + '\'' +
                ", email='" + email + '\'' +
                ", usuario='" + usuario + '\'' +
                ", contraseña='[PROTECTED]'" +
                '}';
    }
}
