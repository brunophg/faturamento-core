package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaResponseDTO;
import com.faturamento.faturamento_core.domain.exception.CnpjDuplicadoException;
import com.faturamento.faturamento_core.domain.exception.CnpjInvalidoException;
import com.faturamento.faturamento_core.domain.exception.EmpresaNaoEncontradaException;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<EmpresaResponseDTO> listarTodos() {
        return empresaRepository.findAll()
                .stream()
                .map(EmpresaResponseDTO::fromEntity)
                .toList();
    }

    public EmpresaResponseDTO buscarPorId(long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Não existe uma empresa cadastrada com o Id: " + id));
        return EmpresaResponseDTO.fromEntity(empresa);

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
    @Transactional
    public EmpresaResponseDTO atualizarEmpresa(Long id, EmpresaRequestDTO request) {
        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Não existe uma empresa cadastrada com o Id: " + id));
        if (!empresaExistente.getCnpj().equals(request.cnpj())
    }
}
