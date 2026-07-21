package com.carlosdaniel.encurtador_url;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A URL original pode ser bem grande, por isso aumentamos o tamanho da coluna
    @Column(nullable = false, length = 2048)
    private String urlOriginal;

    // O nosso hash curto, que será único no sistema todo
    @Column(nullable = false, unique = true, length = 10)
    private String codigoCurto;

    private LocalDateTime dataCriacao;

    // Construtor vazio exigido pelo Spring/JPA
    public Url() {}

    public Url(String urlOriginal, String codigoCurto) {
        this.urlOriginal = urlOriginal;
        this.codigoCurto = codigoCurto;
        this.dataCriacao = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getUrlOriginal() { return urlOriginal; }
    public String getCodigoCurto() { return codigoCurto; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}
