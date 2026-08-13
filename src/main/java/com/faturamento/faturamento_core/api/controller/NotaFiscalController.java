package com.faturamento.faturamento_core.api.controller;

import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalResponseDTO;
import com.faturamento.faturamento_core.domain.service.NotaFiscalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notas")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @GetMapping("/empresa/{empresaId)")
    public ResponseEntity<List<NotaFiscalResponseDTO>> listarPorEmpresa(@PathVariable long empresaId) {
        List<NotaFiscalResponseDTO> notas = notaFiscalService.buscarPorEmpresa(empresaId);

        return ResponseEntity.status(HttpStatus.OK).body(notas);
    }

}
