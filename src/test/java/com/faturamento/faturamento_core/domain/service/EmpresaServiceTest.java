package com.faturamento.faturamento_core.domain.service;

import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaRequestDTO;
import com.faturamento.faturamento_core.domain.dto.empresa.EmpresaResponseDTO;
import com.faturamento.faturamento_core.domain.exception.CnpjDuplicadoException;
import com.faturamento.faturamento_core.domain.exception.EmpresaNaoEncontradaException;
import com.faturamento.faturamento_core.domain.model.Empresa;
import com.faturamento.faturamento_core.domain.repository.EmpresaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    //  helper de cenário

    private Empresa empresaAtiva(Long id, String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setRazaoSocial("Empresa Teste LTDA");
        empresa.setCnpj(cnpj);
        empresa.setInscricaoEstadual("123456789");
        empresa.setAtivo(true);
        return empresa;
    }

    //  listarTodos

    @Test
    @DisplayName("listarTodos: deve retornar a página de empresas ativas mapeada para DTO")
    void deveListarEmpresasAtivas() {
        Pageable pageable = PageRequest.of(0, 10);
        Empresa empresa = empresaAtiva(1L, "12345678000199");
        Page<Empresa> paginaMock = new PageImpl<>(List.of(empresa), pageable, 1);

        when(empresaRepository.findAllByAtivoTrue(pageable)).thenReturn(paginaMock);

        Page<EmpresaResponseDTO> resultado = empresaService.listarTodos(pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals("12345678000199", resultado.getContent().get(0).cnpj());
    }

    //  buscarPorId

    @Test
    @DisplayName("buscarPorId: deve retornar a empresa quando o id existe")
    void deveRetornarEmpresaQuandoIdExiste() {
        Empresa empresa = empresaAtiva(1L, "12345678000199");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        EmpresaResponseDTO response = empresaService.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("Empresa Teste LTDA", response.razaoSocial());
        assertEquals("12345678000199", response.cnpj());
    }

    @Test
    @DisplayName("buscarPorId: deve lançar EmpresaNaoEncontradaException quando o id não existe")
    void deveLancarExcecaoQuandoIdNaoExiste() {
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNaoEncontradaException.class, () -> empresaService.buscarPorId(999L));
    }

    //  salvarEmpresa

    @Test
    @DisplayName("salvarEmpresa: deve salvar com sucesso e devolver razão social e CNPJ nos campos certos")
    void deveSalvarEmpresaComSucessoQuandoCnpjNaoExiste() {
        EmpresaRequestDTO request = new EmpresaRequestDTO("Empresa Nova LTDA", "12345678000199", "123456789");

        when(empresaRepository.existsByCnpj("12345678000199")).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> {
            Empresa salva = invocation.getArgument(0);
            salva.setId(1L); // simula o id gerado pelo banco
            return salva;
        });

        EmpresaResponseDTO response = empresaService.salvarEmpresa(request);

        assertEquals(1L, response.id());
        // Estes dois asserts são os que pegam um bug de ordem de argumentos no construtor do DTO
        assertEquals("Empresa Nova LTDA", response.razaoSocial());
        assertEquals("12345678000199", response.cnpj());
    }

    @Test
    @DisplayName("salvarEmpresa: deve lançar CnpjDuplicadoException quando o CNPJ já existe")
    void deveLancarExcecaoQuandoCnpjJaExiste() {
        EmpresaRequestDTO request = new EmpresaRequestDTO("Empresa Nova LTDA", "12345678000199", "123456789");

        when(empresaRepository.existsByCnpj("12345678000199")).thenReturn(true);

        assertThrows(CnpjDuplicadoException.class, () -> empresaService.salvarEmpresa(request));
        verify(empresaRepository, never()).save(any());
    }

    //  atualizarEmpresa

    @Test
    @DisplayName("atualizarEmpresa: deve lançar EmpresaNaoEncontradaException quando o id não existe")
    void deveLancarExcecaoAoAtualizarEmpresaInexistente() {
        EmpresaRequestDTO request = new EmpresaRequestDTO("Nova Razão", "12345678000199", "123456789");

        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNaoEncontradaException.class, () -> empresaService.atualizarEmpresa(999L, request));
        verify(empresaRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizarEmpresa: quando o CNPJ não muda, atualiza sem consultar duplicidade")
    void deveAtualizarSemVerificarDuplicidadeQuandoCnpjNaoMudou() {
        Empresa existente = empresaAtiva(1L, "12345678000199");
        EmpresaRequestDTO request = new EmpresaRequestDTO("Razão Atualizada", "12345678000199", "999999999");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmpresaResponseDTO response = empresaService.atualizarEmpresa(1L, request);

        assertEquals("Razão Atualizada", response.razaoSocial());
        assertEquals("12345678000199", response.cnpj());
        // O if de fora só entra na checagem se o CNPJ for diferente do atual
        verify(empresaRepository, never()).existsByCnpj(anyString());
    }

    @Test
    @DisplayName("atualizarEmpresa: quando o CNPJ muda para um valor disponível, atualiza com sucesso")
    void deveAtualizarQuandoNovoCnpjEstaDisponivel() {
        Empresa existente = empresaAtiva(1L, "12345678000199");
        EmpresaRequestDTO request = new EmpresaRequestDTO("Razão Atualizada", "99999999000199", "999999999");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(empresaRepository.existsByCnpj("99999999000199")).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmpresaResponseDTO response = empresaService.atualizarEmpresa(1L, request);

        assertEquals("99999999000199", response.cnpj());
        verify(empresaRepository).existsByCnpj("99999999000199");
    }

    @Test
    @DisplayName("atualizarEmpresa: quando o CNPJ muda para um já usado por outra empresa, lança CnpjDuplicadoException")
    void deveLancarExcecaoQuandoNovoCnpjJaPertenceAOutraEmpresa() {
        Empresa existente = empresaAtiva(1L, "12345678000199");
        EmpresaRequestDTO request = new EmpresaRequestDTO("Razão Atualizada", "99999999000199", "999999999");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(empresaRepository.existsByCnpj("99999999000199")).thenReturn(true);

        assertThrows(CnpjDuplicadoException.class, () -> empresaService.atualizarEmpresa(1L, request));
        verify(empresaRepository, never()).save(any());
    }

    //  inativarEmpresa

    @Test
    @DisplayName("inativarEmpresa: deve marcar a empresa como inativa e persistir")
    void deveInativarEmpresaComSucesso() {
        Empresa existente = empresaAtiva(1L, "12345678000199");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(existente));

        empresaService.inativarEmpresa(1L);

        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaRepository).save(captor.capture());
        assertFalse(captor.getValue().getAtivo());
    }

    @Test
    @DisplayName("inativarEmpresa: deve lançar EmpresaNaoEncontradaException quando o id não existe")
    void deveLancarExcecaoAoInativarEmpresaInexistente() {
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNaoEncontradaException.class, () -> empresaService.inativarEmpresa(999L));
        verify(empresaRepository, never()).save(any());
    }
}