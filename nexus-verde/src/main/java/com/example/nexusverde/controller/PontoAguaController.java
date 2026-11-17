package com.example.nexusverde.controller;

import com.example.nexusverde.entity.PontoAgua;
import com.example.nexusverde.service.PontoAguaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pontos-agua")
public class PontoAguaController {

    private final PontoAguaService service;

    public PontoAguaController(PontoAguaService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public String statusApi() {
        return "Nexus Verde API funcionando com sucesso!";
    }

    @GetMapping
    public List<PontoAgua> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoAgua> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PontoAgua criar(@RequestBody PontoAgua ponto) {
        return service.criar(ponto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoAgua> atualizar(@PathVariable Long id, @RequestBody PontoAgua dados) {
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
