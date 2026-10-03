package com.github.joseprandj.OrderFlow_Produto.controller;

import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoAtualizacaoParcialRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoAtualizacaoRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoRequest;
import com.github.joseprandj.OrderFlow_Produto.dto.ProdutoResponse;
import com.github.joseprandj.OrderFlow_Produto.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@RequestBody @Valid ProdutoRequest request) {
        ProdutoResponse response = produtoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<ProdutoResponse> listar(@RequestParam(required = false) Boolean ativo, Pageable pageable) {
        return produtoService.listar(ativo, pageable);
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscarPorId(@PathVariable UUID id) {
        return produtoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable UUID id, @RequestBody @Valid ProdutoAtualizacaoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @PatchMapping("/{id}")
    public ProdutoResponse atualizarParcialmente(@PathVariable UUID id,
                                                 @RequestBody @Valid ProdutoAtualizacaoParcialRequest request) {
        return produtoService.atualizarParcialmente(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        produtoService.excluir(id);
    }
}
