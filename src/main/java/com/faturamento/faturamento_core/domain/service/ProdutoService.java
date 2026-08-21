package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.produto.ProdutoRequestDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoResponseDTO;
import com.faturamento.faturamento_core.domain.exception.ProdutoDuplicadoException;
import com.faturamento.faturamento_core.domain.model.Produto;
import com.faturamento.faturamento_core.domain.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
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
}
