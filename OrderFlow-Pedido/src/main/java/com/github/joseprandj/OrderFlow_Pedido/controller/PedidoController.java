package com.github.joseprandj.OrderFlow_Pedido.controller;

import com.github.joseprandj.OrderFlow_Pedido.dto.ItemPedidoQuantidadeRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.ItemPedidoRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.PedidoRequest;
import com.github.joseprandj.OrderFlow_Pedido.dto.PedidoResponse;
import com.github.joseprandj.OrderFlow_Pedido.service.PedidoService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoRequest request) {
        PedidoResponse response = pedidoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable UUID id) {
        return pedidoService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        pedidoService.excluir(id);
    }

    @PostMapping("/{id}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse incluirItem(@PathVariable UUID id, @RequestBody @Valid ItemPedidoRequest request) {
        return pedidoService.incluirItem(id, request);
    }

    @PatchMapping("/{id}/itens/{idItem}")
    public PedidoResponse alterarItem(@PathVariable UUID id, @PathVariable UUID idItem,
                                      @RequestBody @Valid ItemPedidoQuantidadeRequest request) {
        return pedidoService.alterarItem(id, idItem, request);
    }

    @DeleteMapping("/{id}/itens/{idItem}")
    public PedidoResponse removerItem(@PathVariable UUID id, @PathVariable UUID idItem) {
        return pedidoService.removerItem(id, idItem);
    }
}
