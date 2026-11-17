package com.example.nexusverde;

import com.example.nexusverde.entity.PontoAgua;
import com.example.nexusverde.entity.RegiaoAfetada;
import com.example.nexusverde.repository.PontoAguaRepository;
import com.example.nexusverde.service.PontoAguaService;
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
class PontoAguaServiceTest {

    @Mock
    private PontoAguaRepository pontoRepository;

    @InjectMocks
    private PontoAguaService pontoService;

    @Test
    void deveCriarPontoAgua() {
        RegiaoAfetada regiao = new RegiaoAfetada(1L, "Baixada Fluminense", "Nova Iguacu", "RJ",
                25000, "ALTO", "ATIVA", -22.7596, -43.4510, null);
        PontoAgua ponto = new PontoAgua(null, "Caixa Dagua Central", "Praca Central, s/n",
                "Nova Iguacu", "RJ", "CAIXA_DAGUA", 50000L, "DISPONIVEL", null, regiao);
        PontoAgua salvo = new PontoAgua(1L, "Caixa Dagua Central", "Praca Central, s/n",
                "Nova Iguacu", "RJ", "CAIXA_DAGUA", 50000L, "DISPONIVEL", null, regiao);

        when(pontoRepository.save(any(PontoAgua.class))).thenReturn(salvo);

        PontoAgua resultado = pontoService.criar(ponto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Caixa Dagua Central", resultado.getNome());
        verify(pontoRepository, times(1)).save(ponto);
    }

    @Test
    void deveBuscarPontoAguaPorId() {
        RegiaoAfetada regiao = new RegiaoAfetada(1L, "Baixada Fluminense", "Nova Iguacu", "RJ",
                25000, "ALTO", "ATIVA", -22.7596, -43.4510, null);
        PontoAgua ponto = new PontoAgua(1L, "Poco Comunitario Sao Jose", "Rua Sao Jose, 120",
                "Lajeado", "RS", "POCO", 20000L, "DISPONIVEL", null, regiao);

        when(pontoRepository.findById(1L)).thenReturn(Optional.of(ponto));

        Optional<PontoAgua> resultado = pontoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Poco Comunitario Sao Jose", resultado.get().getNome());
    }
}
