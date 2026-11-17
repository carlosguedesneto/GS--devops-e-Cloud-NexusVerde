package com.example.nexusverde.repository;

import com.example.nexusverde.entity.PontoAgua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PontoAguaRepository extends JpaRepository<PontoAgua, Long> {
}
