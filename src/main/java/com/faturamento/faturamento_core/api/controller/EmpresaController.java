package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaResponseDTO;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import com.faturamento.faturamento_core.domain.service.EmpresaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/empresas")
@SecurityRequirement(name = "bearerAuth")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas(){
        List<EmpresaResponseDTO> empresas = empresaService.listarTodos();

        return ResponseEntity.ok().body(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable long id) {
        EmpresaResponseDTO empresa = empresaService.buscarPorId(id);

        return ResponseEntity.ok().body(empresa);
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> cadastrar(@RequestBody @Valid EmpresaRequestDTO request, UriComponentsBuilder uriBuilder) {
        EmpresaResponseDTO empresa = empresaService.salvarEmpresa(request);
        var uri = uriBuilder.path("/empresas/{id}").buildAndExpand(empresa.id()).toUri();

        return ResponseEntity.created(uri).body(empresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> atualizar(@PathVariable long id, @RequestBody @Valid EmpresaRequestDTO request) {
        EmpresaResponseDTO empresaAtualizada = empresaService.atualizarEmpresa(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(empresaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) {
        empresaService.excluirEmpresa(id);

        return ResponseEntity.noContent().build();
    }
}
