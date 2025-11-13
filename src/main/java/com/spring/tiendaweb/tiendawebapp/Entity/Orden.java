package com.spring.tiendaweb.tiendawebapp.Entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String numero;
    private Date FechaCreacion;
    private Date FechaRecibida;
    private Double total;

    @ManyToOne
    private Usuario usuario;

    @OneToOne(mappedBy = "orden")
    private DetalleOrden detalle;

    public Orden(){}

    public Orden(long id, String numero, Date fechaCreacion, Date fechaRecibida, Double total) {
        this.id = id;
        this.numero = numero;
        FechaCreacion = fechaCreacion;
        FechaRecibida = fechaRecibida;
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getFechaCreacion() {
        return FechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        FechaCreacion = fechaCreacion;
    }

    public Date getFechaRecibida() {
        return FechaRecibida;
    }

    public void setFechaRecibida(Date fechaRecibida) {
        FechaRecibida = fechaRecibida;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
  

    @Override
    public String toString() {
        return "Orden [id=" + id + ", numero=" + numero + ", FechaCreacion=" + FechaCreacion + ", FechaRecibida="
                + FechaRecibida + ", total=" + total + "]";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public DetalleOrden getDetalle() {
        return detalle;
    }

    public void setDetalle(DetalleOrden detalle) {
        this.detalle = detalle;
    }

    

}
