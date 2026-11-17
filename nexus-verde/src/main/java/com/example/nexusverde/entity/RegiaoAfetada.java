package com.example.nexusverde.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "regioes_afetadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegiaoAfetada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(name = "populacao_afetada")
    private Integer populacaoAfetada;

    @Column(name = "nivel_risco", nullable = false, length = 20)
    private String nivelRisco;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(precision = 9, scale = 6)
    private Double latitude;

    @Column(precision = 9, scale = 6)
    private Double longitude;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @PrePersist
    protected void onCreate() {
        if (dataRegistro == null) {
            dataRegistro = LocalDate.now();
        }
    }
}
