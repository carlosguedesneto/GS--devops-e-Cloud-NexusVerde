package com.example.nexusverde.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "solicitacoes_ajuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAjuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String protocolo;

    @Column(name = "nome_solicitante", nullable = false)
    private String nomeSolicitante;

    @Column
    private String contato;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(name = "pessoas_afetadas")
    private Integer pessoasAfetadas;

    @Column
    private Integer prioridade;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "data_solicitacao")
    private LocalDate dataSolicitacao;

    @ManyToOne
    @JoinColumn(name = "regiao_id", nullable = false)
    private RegiaoAfetada regiao;

    @PrePersist
    protected void onCreate() {
        if (dataSolicitacao == null) {
            dataSolicitacao = LocalDate.now();
        }
    }
}
