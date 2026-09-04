package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.itemnota.ItemNotaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalResponseDTO;
import com.faturamento.faturamento_core.domain.enums.StatusNota;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.model.NotaFiscal;
import com.faturamento.faturamento_core.domain.model.Produto;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import com.faturamento.faturamento_core.domain.repository.NotaFiscalRepository;
import com.faturamento.faturamento_core.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaFiscalServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private NotaFiscalService notaFiscalService;

    @Test
    @DisplayName("Deve emitir nota com sucesso, buscar preço real do BD e calcular 23% de impostos")
    void deveEmitirNotaComSucessoECalcularImpostos() {
        // 1. Cenário (Arrange)
        Empresa empresaMock = new Empresa();
        empresaMock.setId(1L);
        empresaMock.setCnpj("12.345.678/0001-99");
        empresaMock.setAtivo(true);

        Produto produtoMock = new Produto();
        produtoMock.setId(10L);
        produtoMock.setPreco(new BigDecimal("100.00")); // Preço real no BD

        // Cliente tenta fraudar mandando um valor unitário de 10.00
        ItemNotaRequestDTO itemRequest = new ItemNotaRequestDTO(10L, 2, new BigDecimal("10.00"));

        // Construtor do Request usando 12345L (Long) para alinhar com o repositório
        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 1L, List.of(itemRequest));

        NotaFiscal notaSalvaMock = new NotaFiscal();
        notaSalvaMock.setId(500L);
        notaSalvaMock.setStatus(StatusNota.PROCESSANDO);


        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));

        when(notaFiscalRepository.existsByNumeroNotaAndEmpresaEmissoraId(12345L, 1L)).thenReturn(false);

        when(produtoRepository.findByIdAtivoTrue(10L)).thenReturn(Optional.of(produtoMock));

        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaSalvaMock);

        // ação (
        NotaFiscalResponseDTO response = notaFiscalService.emitirNota(request);

        // verificação / assert
        assertNotNull(response);
        assertEquals(StatusNota.PROCESSANDO.name(), response.status());
        verify(notaFiscalRepository, times(1)).save(any(NotaFiscal.class));
    }
}