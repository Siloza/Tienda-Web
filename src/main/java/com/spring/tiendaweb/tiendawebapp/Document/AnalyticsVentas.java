package com.spring.tiendaweb.tiendawebapp.Document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 📊 DOCUMENTO MONGODB: Analytics individuales de ventas
 * 
 * Almacena cada consulta analítica realizada desde MySQL
 * con su resultado correspondiente y metadatos
 */
@Document(collection = "analytics_ventas")
public class AnalyticsVentas {

    @Id
    private String id;

    @Field("tipo_consulta")
    private String tipoConsulta; // "total_ventas", "ventas_por_categoria", "ventas_temporales", "top_productos"

    @Field("periodo")
    private String periodo; // "global", "diario", "semanal", "mensual", "anual"

    @Field("resultado")
    private Object resultado; // JSON flexible para almacenar cualquier tipo de resultado

    @Field("fecha_consulta")
    private LocalDateTime fechaConsulta;

    @Field("es_valido")
    private Boolean esValido = true; // Para invalidar analytics antiguos

    @Field("metadata")
    private String metadata; // Información adicional sobre la consulta

    // Constructores
    public AnalyticsVentas() {
        this.fechaConsulta = LocalDateTime.now();
        this.esValido = true;
    }

    public AnalyticsVentas(String tipoConsulta, String periodo, Object resultado) {
        this();
        this.tipoConsulta = tipoConsulta;
        this.periodo = periodo;
        this.resultado = resultado;
    }

    public AnalyticsVentas(String tipoConsulta, String periodo, Object resultado, String metadata) {
        this(tipoConsulta, periodo, resultado);
        this.metadata = metadata;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Object getResultado() { return resultado; }
    public void setResultado(Object resultado) { this.resultado = resultado; }

    public LocalDateTime getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDateTime fechaConsulta) { this.fechaConsulta = fechaConsulta; }

    public Boolean getEsValido() { return esValido; }
    public void setEsValido(Boolean esValido) { this.esValido = esValido; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    @Override
    public String toString() {
        return "AnalyticsVentas{" +
                "id='" + id + '\'' +
                ", tipoConsulta='" + tipoConsulta + '\'' +
                ", periodo='" + periodo + '\'' +
                ", resultado=" + resultado +
                ", fechaConsulta=" + fechaConsulta +
                ", esValido=" + esValido +
                '}';
    }
}