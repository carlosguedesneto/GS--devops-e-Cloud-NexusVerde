package com.example.nexusverde.repository;

import com.example.nexusverde.entity.SolicitacaoAjuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoAjudaRepository extends JpaRepository<SolicitacaoAjuda, Long> {
}
