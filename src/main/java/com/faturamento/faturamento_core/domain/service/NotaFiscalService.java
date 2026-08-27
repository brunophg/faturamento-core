package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.itemnota.ItemNotaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalRequestDTO;
import com.faturamento.faturamento_core.domain.dto.notafiscal.NotaFiscalResponseDTO;
import com.faturamento.faturamento_core.domain.exception.CnpjInvalidoException;
import com.faturamento.faturamento_core.domain.exception.EmpresaNaoEncontradaException;
import com.faturamento.faturamento_core.domain.exception.NotaFiscalDuplicadaException;
import com.faturamento.faturamento_core.domain.exception.ProdutoNaoEncontradoException;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.model.ItemNota;
import com.faturamento.faturamento_core.domain.model.NotaFiscal;
import com.faturamento.faturamento_core.domain.model.Produto;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import com.faturamento.faturamento_core.domain.repository.NotaFiscalRepository;
import com.faturamento.faturamento_core.domain.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository, EmpresaRepository empresaRepository, ProdutoRepository produtoRepository) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.empresaRepository = empresaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public NotaFiscalResponseDTO emitirNota(NotaFiscalRequestDTO request) {

        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa emissora não encontrada com este Id: " + request.empresaId()));

        // RN1 -> Valida CNPJ nulo / formato com 14 digitos
        if (empresa.getCnpj() == null || !empresa.getCnpj().matches("^\\d{14}$")) {
            throw new CnpjInvalidoException("Cnpj da empresa emissora invalido");
        }

        // RN2 -> Verifica duplicidade da Nota Fiscal no banco de dados
        if (notaFiscalRepository.existsByNumeroNotaAndEmpresaEmissoraId(request.numeroNota(), empresa.getId())) {
            throw new NotaFiscalDuplicadaException("A nota fiscal número " + request.numeroNota() + " já foi emitida para esta empresa.");
        }

        NotaFiscal nota = request.toEntity();
        nota.setEmpresaEmissora(empresa);

        // RN3 -> Cálculo de Impostos

        BigDecimal valorTotalLiquido = BigDecimal.ZERO;

        // Alíquotas simuladas (ISS 5% e ICMS 18% = 23% de impostos totais)
        BigDecimal aliquotaImpostos = new BigDecimal("0.23");

        for (ItemNotaRequestDTO itemDto : request.itens()) {
            Produto produto = produtoRepository.findById(itemDto.produtoId())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado com este Id: " + itemDto.produtoId()));

            ItemNota item = new ItemNota();
            item.setQuantidade(itemDto.quantidade());
            item.setProduto(produto);
            item.setValorUnitario(produto.getPreco());
            item.setNotaFiscal(nota);


            BigDecimal quantidade = BigDecimal.valueOf(item.getQuantidade());
            BigDecimal valorBrutoItem = item.getValorUnitario().multiply(quantidade);
            BigDecimal impostosItem = valorBrutoItem.multiply(aliquotaImpostos);

            // Valor Líquido = Valor Bruto - Impostos
            BigDecimal valorLiquidoItem = valorBrutoItem.subtract(impostosItem);

            valorTotalLiquido = valorTotalLiquido.add(valorLiquidoItem);

            nota.addItem(item);
        }

        // Arredondamento para 2 casas decimais
        valorTotalLiquido = valorTotalLiquido.setScale(2, RoundingMode.HALF_EVEN);
        nota.setValorTotal(valorTotalLiquido);

        NotaFiscal notaSalva = notaFiscalRepository.save(nota);
        return NotaFiscalResponseDTO.fromEntity(notaSalva);
    }

    public List<NotaFiscalResponseDTO> buscarPorEmpresa(long empresaId) {

        if (!empresaRepository.existsById(empresaId)) {
            throw new EmpresaNaoEncontradaException("Empresa emissora não encontrada com este Id: " + empresaId);
        }

        List<NotaFiscalResponseDTO> notas = notaFiscalRepository.findByEmpresaEmissoraId(empresaId)
                .stream()
                .map(NotaFiscalResponseDTO::fromEntity)
                .toList();

        return notas;
    }
}
