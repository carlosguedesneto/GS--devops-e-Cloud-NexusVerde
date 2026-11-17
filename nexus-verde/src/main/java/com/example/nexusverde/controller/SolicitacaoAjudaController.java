package com.example.nexusverde.controller;

import com.example.nexusverde.entity.SolicitacaoAjuda;
import com.example.nexusverde.service.SolicitacaoAjudaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes-ajuda")
public class SolicitacaoAjudaController {

    private final SolicitacaoAjudaService service;

    public SolicitacaoAjudaController(SolicitacaoAjudaService service) {
        this.service = service;
    }

    @GetMapping
    public List<SolicitacaoAjuda> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoAjuda> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SolicitacaoAjuda criar(@RequestBody SolicitacaoAjuda solicitacao) {
        return service.criar(solicitacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitacaoAjuda> atualizar(@PathVariable Long id, @RequestBody SolicitacaoAjuda dados) {
        return service.atualizar(id, dados)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
