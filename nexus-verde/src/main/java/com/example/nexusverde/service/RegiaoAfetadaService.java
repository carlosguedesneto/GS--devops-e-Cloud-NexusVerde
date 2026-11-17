package com.example.nexusverde.service;

import com.example.nexusverde.entity.RegiaoAfetada;
import com.example.nexusverde.repository.RegiaoAfetadaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegiaoAfetadaService {

    private final RegiaoAfetadaRepository repository;

    public RegiaoAfetadaService(RegiaoAfetadaRepository repository) {
        this.repository = repository;
    }

    public List<RegiaoAfetada> listarTodas() {
        return repository.findAll();
    }

    public Optional<RegiaoAfetada> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public RegiaoAfetada criar(RegiaoAfetada regiao) {
        return repository.save(regiao);
    }

    public Optional<RegiaoAfetada> atualizar(Long id, RegiaoAfetada dados) {
        return repository.findById(id).map(regiao -> {
            regiao.setNome(dados.getNome());
            regiao.setMunicipio(dados.getMunicipio());
            regiao.setUf(dados.getUf());
            regiao.setPopulacaoAfetada(dados.getPopulacaoAfetada());
            regiao.setNivelRisco(dados.getNivelRisco());
            regiao.setStatus(dados.getStatus());
            regiao.setLatitude(dados.getLatitude());
            regiao.setLongitude(dados.getLongitude());
            regiao.setDataRegistro(dados.getDataRegistro());
            return repository.save(regiao);
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
