package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.itemnota.ItemNotaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalResponseDTO;
import com.faturamento.faturamento_core.domain.enums.StatusNota;
import com.faturamento.faturamento_core.domain.exception.*;
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
import static org.mockito.ArgumentMatchers.anyLong;
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

    private Empresa empresaValidaEAtiva() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setCnpj("12345678000199");
        empresa.setAtivo(true);
        return empresa;
    }

    private Produto produtoAtivo(Long id, String preco) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setPreco(new BigDecimal(preco));
        produto.setAtivo(true);
        return produto;
    }

    // ---------- emitirNota: caminho feliz ----------

    @Test
    @DisplayName("Deve emitir nota com sucesso, ignorar o valor unitário do request e calcular 23% de impostos")
    void deveEmitirNotaComSucessoECalcularImpostos() {
        Empresa empresaMock = empresaValidaEAtiva();
        Produto produtoMock = produtoAtivo(10L, "100.00");

        // Erro provocado mandando um valor unitário de 10.00 — o service deve ignorar isso
        // e usar o preço real vindo do banco (100.00).
        ItemNotaRequestDTO itemRequest = new ItemNotaRequestDTO(10L, 2, new BigDecimal("10.00"));
        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 1L, List.of(itemRequest));

        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));
        when(notaFiscalRepository.existsByNumeroNotaAndEmpresaEmissoraId(12345L, 1L)).thenReturn(false);
        when(produtoRepository.findByIdAtivoTrue(10L)).thenReturn(Optional.of(produtoMock));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotaFiscalResponseDTO response = notaFiscalService.emitirNota(request);

        assertNotNull(response);
        assertEquals(StatusNota.PROCESSANDO.name(), response.status());
        assertEquals(1, response.itens().size());
        // 100.00 (preço real) * 2 = 200.00 bruto; -23% de imposto = 154.00 líquido
        assertEquals(new BigDecimal("154.00"), response.valorTotal());
        assertEquals(new BigDecimal("100.00"), response.itens().get(0).valorUnitario());
        verify(notaFiscalRepository, times(1)).save(any(NotaFiscal.class));
    }

    @Test
    @DisplayName("Deve somar corretamente o valor líquido de múltiplos itens de produtos diferentes")
    void deveCalcularValorTotalComMultiplosItens() {
        Empresa empresaMock = empresaValidaEAtiva();
        Produto produtoA = produtoAtivo(10L, "100.00"); // 3x => bruto 300.00 -> líquido 231.00
        Produto produtoB = produtoAtivo(20L, "50.00");  // 1x => bruto 50.00  -> líquido 38.50

        ItemNotaRequestDTO itemA = new ItemNotaRequestDTO(10L, 3, null);
        ItemNotaRequestDTO itemB = new ItemNotaRequestDTO(20L, 1, null);
        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12346L, LocalDateTime.now(), 1L, List.of(itemA, itemB));

        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));
        when(notaFiscalRepository.existsByNumeroNotaAndEmpresaEmissoraId(12346L, 1L)).thenReturn(false);
        when(produtoRepository.findByIdAtivoTrue(10L)).thenReturn(Optional.of(produtoA));
        when(produtoRepository.findByIdAtivoTrue(20L)).thenReturn(Optional.of(produtoB));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotaFiscalResponseDTO response = notaFiscalService.emitirNota(request);

        assertEquals(2, response.itens().size());
        assertEquals(new BigDecimal("269.50"), response.valorTotal()); // 231.00 + 38.50
    }

    // ---------- emitirNota: empresa ----------

    @Test
    @DisplayName("Deve lançar EmpresaNaoEncontradaException quando a empresa não existe ou está inativa")
    void deveLancarExcecaoQuandoEmpresaNaoEncontrada() {
        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 999L, List.of());

        when(empresaRepository.findByIdAtivoTrue(999L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNaoEncontradaException.class, () -> notaFiscalService.emitirNota(request));

        // Nada além da busca da empresa deveria ter sido chamado
        verify(notaFiscalRepository, never()).existsByNumeroNotaAndEmpresaEmissoraId(anyLong(), anyLong());
        verify(notaFiscalRepository, never()).save(any());
    }

    // ---------- emitirNota: RN1 (CNPJ) ----------

    @Test
    @DisplayName("RN1: deve lançar CnpjInvalidoException quando o CNPJ da empresa é nulo")
    void deveLancarExcecaoQuandoCnpjNulo() {
        Empresa empresaMock = empresaValidaEAtiva();
        empresaMock.setCnpj(null);

        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 1L, List.of());
        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));

        assertThrows(CnpjInvalidoException.class, () -> notaFiscalService.emitirNota(request));
        verify(notaFiscalRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN1: deve lançar CnpjInvalidoException quando o CNPJ está formatado com máscara em vez de só dígitos")
    void deveLancarExcecaoQuandoCnpjComMascara() {
        Empresa empresaMock = empresaValidaEAtiva();
        empresaMock.setCnpj("12.345.678/0001-99"); // formato inválido para a regra ^\d{14}$

        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 1L, List.of());
        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));

        assertThrows(CnpjInvalidoException.class, () -> notaFiscalService.emitirNota(request));
    }

    // ---------- emitirNota: RN2 (duplicidade) ----------

    @Test
    @DisplayName("RN2: deve lançar NotaFiscalDuplicadaException quando já existe nota com esse número para a empresa")
    void deveLancarExcecaoQuandoNotaDuplicada() {
        Empresa empresaMock = empresaValidaEAtiva();
        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 1L, List.of());

        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));
        when(notaFiscalRepository.existsByNumeroNotaAndEmpresaEmissoraId(12345L, 1L)).thenReturn(true);

        assertThrows(NotaFiscalDuplicadaException.class, () -> notaFiscalService.emitirNota(request));

        // A validação de duplicidade acontece ANTES do loop de itens: produtoRepository nunca deve ser chamado
        verify(produtoRepository, never()).findByIdAtivoTrue(any());
        verify(notaFiscalRepository, never()).save(any());
    }

    // ---------- emitirNota: produto ----------

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException quando um item referencia produto inexistente ou inativo")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        Empresa empresaMock = empresaValidaEAtiva();
        ItemNotaRequestDTO itemRequest = new ItemNotaRequestDTO(999L, 1, null);
        NotaFiscalRequestDTO request = new NotaFiscalRequestDTO(12345L, LocalDateTime.now(), 1L, List.of(itemRequest));

        when(empresaRepository.findByIdAtivoTrue(1L)).thenReturn(Optional.of(empresaMock));
        when(notaFiscalRepository.existsByNumeroNotaAndEmpresaEmissoraId(12345L, 1L)).thenReturn(false);
        when(produtoRepository.findByIdAtivoTrue(999L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> notaFiscalService.emitirNota(request));
        verify(notaFiscalRepository, never()).save(any());
    }


    @Test
    @DisplayName("concluirEmissão: deve mudar o status de PROCESSANDO para EMITIDA")
    void deveConcluirEmissaoQuandoStatusForProcessando() {
        NotaFiscal nota = new NotaFiscal();
        nota.setId(1L);
        nota.setStatus(StatusNota.PROCESSANDO);
        nota.setEmpresaEmissora(empresaValidaEAtiva());
        nota.setValorTotal(new BigDecimal("100.00"));

        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(nota));

        NotaFiscalResponseDTO response = notaFiscalService.concluirEmissão(1L);

        assertEquals(StatusNota.EMITIDA.name(), response.status());
    }

    @Test
    @DisplayName("concluirEmissão: deve lançar StatusNotaInvalidoException se a nota não estiver PROCESSANDO")
    void deveLancarExcecaoAoConcluirNotaQueNaoEstaProcessando() {
        NotaFiscal nota = new NotaFiscal();
        nota.setId(1L);
        nota.setStatus(StatusNota.EMITIDA); // já foi emitida antes

        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(nota));

        assertThrows(StatusNotaInvalidoException.class, () -> notaFiscalService.concluirEmissão(1L));
    }

    @Test
    @DisplayName("concluirEmissão: deve lançar NotaFiscalNaoEncontradaException se o id não existir")
    void deveLancarExcecaoAoConcluirNotaInexistente() {
        when(notaFiscalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotaFiscalNaoEncontradaException.class, () -> notaFiscalService.concluirEmissão(999L));
    }


    @Test
    @DisplayName("cancelar: deve mudar o status para CANCELADA quando a nota não estiver cancelada ainda")
    void deveCancelarNotaComSucesso() {
        NotaFiscal nota = new NotaFiscal();
        nota.setId(1L);
        nota.setStatus(StatusNota.PROCESSANDO);
        nota.setEmpresaEmissora(empresaValidaEAtiva());
        nota.setValorTotal(new BigDecimal("100.00"));

        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(nota));

        NotaFiscalResponseDTO response = notaFiscalService.cancelar(1L);

        assertEquals(StatusNota.CANCELADA.name(), response.status());
    }

    @Test
    @DisplayName("cancelar: deve lançar exceção ao tentar cancelar uma nota já cancelada")
    void deveLancarExcecaoAoCancelarNotaJaCancelada() {
        NotaFiscal nota = new NotaFiscal();
        nota.setId(1L);
        nota.setStatus(StatusNota.CANCELADA);

        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(nota));

        assertThrows(NotaFiscalNaoEncontradaException.class, () -> notaFiscalService.cancelar(1L));
    }
}