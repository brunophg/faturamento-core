package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaResponseDTO;
import com.faturamento.faturamento_core.domain.exception.CnpjDuplicadoException;
import com.faturamento.faturamento_core.domain.exception.CnpjInvalidoException;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public EmpresaResponseDTO salvarEmpresa(EmpresaRequestDTO request) {
        if (empresaRepository.existsByCnpj(request.cnpj())) {
            throw new CnpjDuplicadoException("Já existe uma empresa cadastrada com este CNPJ.");
        }
        Empresa novaEmpresa = request.toEntity();

        Empresa empresaSalva = empresaRepository.save(novaEmpresa);
        return new EmpresaResponseDTO(
                empresaSalva.getId(),
                empresaSalva.getCnpj(),
                empresaSalva.getRazaoSocial(),
                empresaSalva.getInscricaoEstadual()
        );
    }
}
