package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.dto.produto.ProdutoRequestDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoResponseDTO;
import com.faturamento.faturamento_core.domain.dto.produto.ProdutoUpdateDTO;
import com.faturamento.faturamento_core.domain.service.ProdutoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodos(@PageableDefault(size = 20, page = 0, sort = "nome")
                                                                    Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.listarTodos(pageable);

        return ResponseEntity.ok().body(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable long id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);

        return ResponseEntity.ok().body(produto);
    }
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        ProdutoResponseDTO produto = produtoService.buscarPorCodigo(codigo);
        return ResponseEntity.ok().body(produto);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> adicionar(@RequestBody @Valid ProdutoRequestDTO request, UriComponentsBuilder uriBuilder) {
        ProdutoResponseDTO produto = produtoService.adicionarProduto(request);
        var uri = uriBuilder.path("/produtos/{id}").buildAndExpand(produto.id()).toUri();

        return ResponseEntity.created(uri).body(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoUpdateDTO request) {
        ProdutoResponseDTO produto = produtoService.atualizarProduto(id, request);
        return ResponseEntity.ok(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        produtoService.inativarProduto(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ProdutoResponseDTO> reativar(@PathVariable long id) {
        ProdutoResponseDTO produto = produtoService.reativarProduto(id);

        return ResponseEntity.ok().body(produto);
    }
}
