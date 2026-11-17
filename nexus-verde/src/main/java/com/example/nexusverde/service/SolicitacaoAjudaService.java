package com.example.nexusverde.service;

import com.example.nexusverde.entity.SolicitacaoAjuda;
import com.example.nexusverde.repository.SolicitacaoAjudaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SolicitacaoAjudaService {

    private static final AtomicLong CONTADOR = new AtomicLong(1);
    private static final DateTimeFormatter ANO = DateTimeFormatter.ofPattern("yyyy");

    private final SolicitacaoAjudaRepository repository;

    public SolicitacaoAjudaService(SolicitacaoAjudaRepository repository) {
        this.repository = repository;
    }

    public List<SolicitacaoAjuda> listarTodas() {
        return repository.findAll();
    }

    public Optional<SolicitacaoAjuda> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public SolicitacaoAjuda criar(SolicitacaoAjuda solicitacao) {
        if (solicitacao.getProtocolo() == null || solicitacao.getProtocolo().isBlank()) {
            solicitacao.setProtocolo(gerarProtocolo());
        }
        if (solicitacao.getDataSolicitacao() == null) {
            solicitacao.setDataSolicitacao(LocalDate.now());
        }
        if (solicitacao.getStatus() == null || solicitacao.getStatus().isBlank()) {
            solicitacao.setStatus("ABERTA");
        }
        return repository.save(solicitacao);
    }

    public Optional<SolicitacaoAjuda> atualizar(Long id, SolicitacaoAjuda dados) {
        return repository.findById(id).map(solicitacao -> {
            solicitacao.setProtocolo(dados.getProtocolo());
            solicitacao.setNomeSolicitante(dados.getNomeSolicitante());
            solicitacao.setContato(dados.getContato());
            solicitacao.setDescricao(dados.getDescricao());
            solicitacao.setPessoasAfetadas(dados.getPessoasAfetadas());
            solicitacao.setPrioridade(dados.getPrioridade());
            solicitacao.setStatus(dados.getStatus());
            solicitacao.setDataSolicitacao(dados.getDataSolicitacao());
            solicitacao.setRegiao(dados.getRegiao());
            return repository.save(solicitacao);
        });
    }

    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private String gerarProtocolo() {
        return "NV-" + ANO.format(LocalDate.now())
                + "-" + String.format("%04d", CONTADOR.getAndIncrement());
    }
}
