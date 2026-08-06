package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaResponseDTO;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import com.faturamento.faturamento_core.domain.service.EmpresaService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas(){
        List<EmpresaResponseDTO> lista = empresaService.listarTodos();

        return ResponseEntity.ok().body(lista);
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

}
