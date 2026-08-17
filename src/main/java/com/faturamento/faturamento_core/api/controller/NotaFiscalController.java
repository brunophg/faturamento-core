package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalResponseDTO;
import com.faturamento.faturamento_core.domain.service.NotaFiscalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notas")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<NotaFiscalResponseDTO>> listarPorEmpresa(@PathVariable long empresaId) {
        List<NotaFiscalResponseDTO> notas = notaFiscalService.buscarPorEmpresa(empresaId);

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
