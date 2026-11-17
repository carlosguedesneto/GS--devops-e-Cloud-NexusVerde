package com.example.nexusverde.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "pontos_agua")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoAgua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column
    private String endereco;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(name = "capacidade_litros")
    private Long capacidadeLitros;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "regiao_id", nullable = false)
    private RegiaoAfetada regiao;

    @PrePersist
    protected void onCreate() {
        if (dataAtualizacao == null) {
            dataAtualizacao = LocalDate.now();
        }
    }
}
