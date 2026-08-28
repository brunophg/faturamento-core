package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalResponseDTO;
import com.faturamento.faturamento_core.domain.service.NotaFiscalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notas")
@SecurityRequirement(name = "bearerAuth")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<NotaFiscalResponseDTO>> listarPorEmpresa(@PageableDefault(size = 20, page = 0, sort = "id")Pageable pageable, @PathVariable long empresaId) {
        Page<NotaFiscalResponseDTO> notas = notaFiscalService.buscarPorEmpresa(pageable, empresaId);

        return ResponseEntity.status(HttpStatus.OK).body(notas);
    }

    @PostMapping
    public ResponseEntity<NotaFiscalResponseDTO> emitir(@RequestBody @Valid NotaFiscalRequestDTO request) {

        NotaFiscalResponseDTO notaNova = notaFiscalService.emitirNota(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(notaNova.id())
                .toUri();

        return ResponseEntity.created(uri).body(notaNova);
    }

}
