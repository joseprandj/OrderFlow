package com.github.joseprandj.OrderFlow_Estoque.controller;

import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueCriacaoRequest;
import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueQuantidadeRequest;
import com.github.joseprandj.OrderFlow_Estoque.dto.EstoqueResponse;
import com.github.joseprandj.OrderFlow_Estoque.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    /**
     * Utilizado pelo domínio Produto para criar o estoque inicial (quantidade 0) de um novo SKU.
     */
    @PostMapping
    public ResponseEntity<EstoqueResponse> criar(@RequestBody @Valid EstoqueCriacaoRequest request) {
        EstoqueResponse response = estoqueService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{sku}")
                .buildAndExpand(response.sku())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EstoqueResponse>> listar(@RequestParam(required = false) Integer quantidadeMinima, Pageable pageable) {
        return ResponseEntity.ok(estoqueService.listar(quantidadeMinima, pageable));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<EstoqueResponse> buscarPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(estoqueService.buscarPorSku(sku));
    }

    @PatchMapping("/{sku}")
    public ResponseEntity<EstoqueResponse> atualizarQuantidade(@PathVariable String sku,
                                                               @RequestBody @Valid EstoqueQuantidadeRequest request) {
        return ResponseEntity.ok(estoqueService.atualizarQuantidade(sku, request));
    }
}
