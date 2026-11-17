package com.example.nexusverde;

import com.example.nexusverde.entity.RegiaoAfetada;
import com.example.nexusverde.entity.SolicitacaoAjuda;
import com.example.nexusverde.repository.SolicitacaoAjudaRepository;
import com.example.nexusverde.service.SolicitacaoAjudaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAjudaServiceTest {

    @Mock
    private SolicitacaoAjudaRepository solicitacaoRepository;

    @InjectMocks
    private SolicitacaoAjudaService solicitacaoService;

    @Test
    void deveCriarSolicitacao() {
        RegiaoAfetada regiao = new RegiaoAfetada(1L, "Baixada Fluminense", "Nova Iguacu", "RJ",
                25000, "ALTO", "ATIVA", -22.7596, -43.4510, null);
        SolicitacaoAjuda solicitacao = new SolicitacaoAjuda(null, null, "Maria Silva", "(21) 99999-0001",
                "Falta de agua ha 3 dias no bairro Nova Iguacu.", 4, 1, null, null, regiao);
        SolicitacaoAjuda salva = new SolicitacaoAjuda(1L, "NV-2026-0001", "Maria Silva", "(21) 99999-0001",
                "Falta de agua ha 3 dias no bairro Nova Iguacu.", 4, 1, "ABERTA", null, regiao);

        ArgumentCaptor<SolicitacaoAjuda> captor = ArgumentCaptor.forClass(SolicitacaoAjuda.class);
        when(solicitacaoRepository.save(captor.capture())).thenReturn(salva);

        SolicitacaoAjuda resultado = solicitacaoService.criar(solicitacao);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNotNull(captor.getValue().getProtocolo());
        assertTrue(captor.getValue().getProtocolo().startsWith("NV-"));
        assertEquals("ABERTA", captor.getValue().getStatus());
        verify(solicitacaoRepository, times(1)).save(any(SolicitacaoAjuda.class));
    }

    @Test
    void deveBuscarSolicitacaoPorId() {
        RegiaoAfetada regiao = new RegiaoAfetada(1L, "Vale do Taquari", "Lajeado", "RS",
                12000, "CRITICO", "ATIVA", -29.4669, -51.9619, null);
        SolicitacaoAjuda solicitacao = new SolicitacaoAjuda(1L, "NV-2026-0002", "Joao Souza", "(51) 99999-0002",
                "Comunidade isolada pela enchente, necessita de agua potavel.", 22, 1, "EM_ATENDIMENTO", null, regiao);

        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        Optional<SolicitacaoAjuda> resultado = solicitacaoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Joao Souza", resultado.get().getNomeSolicitante());
    }
}
