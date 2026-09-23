package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.produto.ProdutoRequestDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoResponseDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoUpdateDTO;
import com.faturamento.faturamento_core.domain.exception.ProdutoDuplicadoException;
import com.faturamento.faturamento_core.domain.exception.ProdutoNaoEncontradoException;
import com.faturamento.faturamento_core.domain.model.Produto;
import com.faturamento.faturamento_core.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    //  helper de cenário

    private Produto produtoAtivo(Long id, String codigo, String preco) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setCodigo(codigo);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição de teste");
        produto.setPreco(new BigDecimal(preco));
        produto.setAtivo(true);
        return produto;
    }

    //  listarTodos
    @Test
    @DisplayName("listarTodos: deve retornar a página de produtos ativos mapeada para DTO")
    void deveListarTodosOsProdutosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Produto produto = produtoAtivo(1L, "COD1", "10.00");
        Page<Produto> paginaMock = new PageImpl<>(List.of(produto), pageable, 1);

        when(produtoRepository.findAllByAtivoTrue(pageable)).thenReturn(paginaMock);

        Page<ProdutoResponseDTO> resultado = produtoService.listarTodos(pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals("COD1", resultado.getContent().get(0).codigo());
    }

    //  buscarPorId

    @Test
    @DisplayName("buscarPorId: deve retornar o produto quando o id existe")
    void deveRetornarProdutoQuandoIdExistente() {
        long produtoId = 1L;
        Produto produtoMock = produtoAtivo(produtoId, "COD1", "199.90");

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoMock));

        ProdutoResponseDTO response = produtoService.buscarPorId(produtoId);

        assertNotNull(response);
        assertEquals(produtoId, response.id());
        assertEquals(new BigDecimal("199.90"), response.preco());
    }

    @Test
    @DisplayName("buscarPorId: deve lançar ProdutoNaoEncontradoException quando o id não existe")
    void deveLancarExcecaoQuandoIdNaoExiste() {
        long produtoId = 999L;

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(produtoId));
    }

    // buscarPorCodigo

    @Test
    @DisplayName("buscarPorCodigo: deve retornar o produto quando o código existe")
    void deveRetornarProdutoQuandoCodigoExiste() {
        String codigo = "COD1";
        Produto produtoMock = produtoAtivo(1L, codigo, "50.00");

        when(produtoRepository.findByCodigo(codigo)).thenReturn(Optional.of(produtoMock));

        ProdutoResponseDTO response = produtoService.buscarPorCodigo(codigo);

        assertNotNull(response);
        assertEquals(codigo, response.codigo());
    }

    @Test
    @DisplayName("buscarPorCodigo: deve lançar ProdutoNaoEncontradoException quando o código não existe")
    void deveLancarExcecaoQuandoCodigoNaoExiste() {
        String codigo = "INEXISTENTE";

        when(produtoRepository.findByCodigo(codigo)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorCodigo(codigo));
    }

    // adicionarProduto

    @Test
    @DisplayName("adicionarProduto: deve salvar com sucesso quando o código ainda não existe")
    void deveSalvarProdutoComSucessoQuandoCodigoNaoExiste() {
        ProdutoRequestDTO request = new ProdutoRequestDTO("COD1", "Produto Novo", "Descrição", new BigDecimal("29.90"));

        when(produtoRepository.existsByCodigo("COD1")).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto produtoSalvo = invocation.getArgument(0);
            produtoSalvo.setId(1L);
            return produtoSalvo;
        });

        ProdutoResponseDTO response = produtoService.adicionarProduto(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("COD1", response.codigo());
        assertEquals(new BigDecimal("29.90"), response.preco());
        assertTrue(response.ativo());
    }

    @Test
    @DisplayName("adicionarProduto: deve lançar ProdutoDuplicadoException quando o código já existe")
    void deveLancarExcecaoQuandoCodigoJaExiste() {
        ProdutoRequestDTO request = new ProdutoRequestDTO("COD1", "Produto Novo", "Descrição", new BigDecimal("29.90"));

        when(produtoRepository.existsByCodigo("COD1")).thenReturn(true);

        assertThrows(ProdutoDuplicadoException.class, () -> produtoService.adicionarProduto(request));

        // A validação de duplicidade acontece antes de qualquer tentativa de salvar
        verify(produtoRepository, never()).save(any());
    }

    //  atualizarProduto

    @Test
    @DisplayName("atualizarProduto: deve atualizar nome, descrição e preço quando o id existe")
    void deveAtualizarProdutoComSucesso() {
        long produtoId = 1L;
        Produto produtoExistente = produtoAtivo(produtoId, "COD1", "10.00");
        ProdutoUpdateDTO request = new ProdutoUpdateDTO("Nome Atualizado", "Descrição Atualizada", new BigDecimal("20.00"));

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoResponseDTO response = produtoService.atualizarProduto(produtoId, request);

        assertEquals("Nome Atualizado", response.nome());
        assertEquals("Descrição Atualizada", response.descricao());
        assertEquals(new BigDecimal("20.00"), response.preco());
        // O código não faz parte do ProdutoUpdateDTO — confirma que continua o mesmo, sem ser zerado
        assertEquals("COD1", response.codigo());
    }

    @Test
    @DisplayName("atualizarProduto: deve lançar ProdutoNaoEncontradoException quando o id não existe")
    void deveLancarExcecaoQuandoIdNaoExisteAoAtualizar() {
        long produtoId = 999L;
        ProdutoUpdateDTO request = new ProdutoUpdateDTO("Nome", "Descrição", new BigDecimal("20.00"));

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.atualizarProduto(produtoId, request));
        verify(produtoRepository, never()).save(any());
    }

    // inativarProduto

    @Test
    @DisplayName("inativarProduto: deve marcar o produto como inativo e persistir a alteração")
    void deveInativarProdutoComSucesso() {
        long produtoId = 1L;
        Produto produtoExistente = produtoAtivo(produtoId, "COD1", "10.00");

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoExistente));

        produtoService.inativarProduto(produtoId);

        // inativarProduto() é void — não há retorno para assertar, então a prova de que
        // captura o objeto passado para save() e confirmar seu estado.
        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());
        assertFalse(captor.getValue().getAtivo());
    }

    @Test
    @DisplayName("inativarProduto: deve lançar ProdutoNaoEncontradoException quando o id não existe")
    void deveLancarExcecaoAoInativarProdutoInexistente() {
        long produtoId = 999L;

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.inativarProduto(produtoId));
        verify(produtoRepository, never()).save(any());
    }

    //  reativarProduto

    @Test
    @DisplayName("reativarProduto: deve marcar o produto como ativo e retornar o DTO atualizado")
    void deveReativarProdutoComSucesso() {
        long produtoId = 1L;
        Produto produtoExistente = produtoAtivo(produtoId, "COD1", "10.00");
        produtoExistente.setAtivo(false); // estava inativo antes da chamada

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produtoExistente));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoResponseDTO response = produtoService.reativarProduto(produtoId);

        assertTrue(response.ativo());
    }

    @Test
    @DisplayName("reativarProduto: deve lançar ProdutoNaoEncontradoException quando o id não existe")
    void deveLancarExcecaoAoReativarProdutoInexistente() {
        long produtoId = 999L;

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.reativarProduto(produtoId));
        verify(produtoRepository, never()).save(any());
    }
}