package com.github.joseprandj.OrderFlow_Financeiro.controller;

import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroAtualizacaoRequest;
import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroRequest;
import com.github.joseprandj.OrderFlow_Financeiro.dto.FinanceiroResponse;
import com.github.joseprandj.OrderFlow_Financeiro.service.FinanceiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/financeiros")
@RequiredArgsConstructor
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    @PostMapping
    public ResponseEntity<FinanceiroResponse> criar(@RequestBody @Valid FinanceiroRequest request) {
        FinanceiroResponse response = financeiroService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<FinanceiroResponse> listar(Pageable pageable) {
        return financeiroService.listar(pageable);
    }

    @GetMapping("/{id}")
    public FinanceiroResponse buscarPorId(@PathVariable UUID id) {
        return financeiroService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public FinanceiroResponse atualizar(@PathVariable UUID id, @RequestBody @Valid FinanceiroAtualizacaoRequest request) {
        return financeiroService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        financeiroService.excluir(id);
    }
}
