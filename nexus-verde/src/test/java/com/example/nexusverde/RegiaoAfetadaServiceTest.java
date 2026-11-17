package com.example.nexusverde;

import com.example.nexusverde.entity.RegiaoAfetada;
import com.example.nexusverde.repository.RegiaoAfetadaRepository;
import com.example.nexusverde.service.RegiaoAfetadaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegiaoAfetadaServiceTest {

    @Mock
    private RegiaoAfetadaRepository regiaoRepository;

    @InjectMocks
    private RegiaoAfetadaService regiaoService;

    @Test
    void deveCriarRegiao() {
        RegiaoAfetada regiao = new RegiaoAfetada(null, "Baixada Fluminense", "Nova Iguacu", "RJ",
                25000, "ALTO", "ATIVA", -22.7596, -43.4510, null);
        RegiaoAfetada salva = new RegiaoAfetada(1L, "Baixada Fluminense", "Nova Iguacu", "RJ",
                25000, "ALTO", "ATIVA", -22.7596, -43.4510, null);

        when(regiaoRepository.save(any(RegiaoAfetada.class))).thenReturn(salva);

        RegiaoAfetada resultado = regiaoService.criar(regiao);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Baixada Fluminense", resultado.getNome());
        verify(regiaoRepository, times(1)).save(regiao);
    }

    @Test
    void deveBuscarRegiaoPorId() {
        RegiaoAfetada regiao = new RegiaoAfetada(1L, "Vale do Taquari", "Lajeado", "RS",
                12000, "CRITICO", "ATIVA", -29.4669, -51.9619, null);

        when(regiaoRepository.findById(1L)).thenReturn(Optional.of(regiao));

        Optional<RegiaoAfetada> resultado = regiaoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Vale do Taquari", resultado.get().getNome());
    }
}
