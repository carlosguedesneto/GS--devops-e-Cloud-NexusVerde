package com.example.nexusverde.controller;

import com.example.nexusverde.entity.RegiaoAfetada;
import com.example.nexusverde.service.RegiaoAfetadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regioes-afetadas")
public class RegiaoAfetadaController {

    private final RegiaoAfetadaService service;

    public RegiaoAfetadaController(RegiaoAfetadaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RegiaoAfetada> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegiaoAfetada> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RegiaoAfetada criar(@RequestBody RegiaoAfetada regiao) {
        return service.criar(regiao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegiaoAfetada> atualizar(@PathVariable Long id, @RequestBody RegiaoAfetada dados) {
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
