package com.example.nexusverde.service;

import com.example.nexusverde.entity.PontoAgua;
import com.example.nexusverde.repository.PontoAguaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PontoAguaService {

    private final PontoAguaRepository repository;

    public PontoAguaService(PontoAguaRepository repository) {
        this.repository = repository;
    }

    public List<PontoAgua> listarTodos() {
        return repository.findAll();
    }

    public Optional<PontoAgua> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public PontoAgua criar(PontoAgua ponto) {
        return repository.save(ponto);
    }

    public Optional<PontoAgua> atualizar(Long id, PontoAgua dados) {
        return repository.findById(id).map(ponto -> {
            ponto.setNome(dados.getNome());
            ponto.setEndereco(dados.getEndereco());
            ponto.setMunicipio(dados.getMunicipio());
            ponto.setUf(dados.getUf());
            ponto.setTipo(dados.getTipo());
            ponto.setCapacidadeLitros(dados.getCapacidadeLitros());
            ponto.setStatus(dados.getStatus());
            ponto.setDataAtualizacao(dados.getDataAtualizacao());
            ponto.setRegiao(dados.getRegiao());
            return repository.save(ponto);
        });
    }

    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
