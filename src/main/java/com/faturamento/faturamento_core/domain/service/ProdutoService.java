package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.produto.ProdutoRequestDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoResponseDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoUpdateDTO;
import com.faturamento.faturamento_core.domain.exception.ProdutoDuplicadoException;
import com.faturamento.faturamento_core.domain.exception.ProdutoNaoEncontradoException;
import com.faturamento.faturamento_core.domain.model.Produto;
import com.faturamento.faturamento_core.domain.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarTodos(Pageable pageable) {
        return produtoRepository.findAllByAtivoTrue(pageable)
                .map(ProdutoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Não existe um produto cadastrado com o Id: " + id));
        return ProdutoResponseDTO.fromEntity(produto);
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorCodigo(String codigo) {
        Produto produto = produtoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com código '" + codigo + "' não encontrado."));
        return ProdutoResponseDTO.fromEntity(produto);
    }

    @Transactional
    public ProdutoResponseDTO adicionarProduto(ProdutoRequestDTO request) {
        if (produtoRepository.existsByCodigo(request.codigo())) {
            throw new ProdutoDuplicadoException("Ja existe um produto cadastrado com esse código: " + request.codigo());
        }

        Produto novoProduto = request.toEntity();

        Produto produtoSalvo = produtoRepository.save(novoProduto);

        return new ProdutoResponseDTO(
                produtoSalvo.getId(),
                produtoSalvo.getCodigo(),
                produtoSalvo.getNome(),
                produtoSalvo.getDescricao(),
                produtoSalvo.getPreco(),
                produtoSalvo.getAtivo()
        );
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoUpdateDTO request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Não existe um produto cadastrado com o Id: " + id));

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());

        Produto produtoAtualizado = produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(produtoAtualizado);
    }

    @Transactional
    public void inativarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Não existe um produto cadastrado com o Id: " + id));

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoResponseDTO reativarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Não existe um produto cadastrado com o Id: " + id));

        produto.setAtivo(true);
        produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(produto);
    }
}
