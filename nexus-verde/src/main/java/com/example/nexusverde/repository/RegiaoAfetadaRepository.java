package com.example.nexusverde.repository;

import com.example.nexusverde.entity.RegiaoAfetada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoAfetadaRepository extends JpaRepository<RegiaoAfetada, Long> {
}
