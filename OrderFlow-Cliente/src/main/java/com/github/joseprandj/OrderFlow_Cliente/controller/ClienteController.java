package com.github.joseprandj.OrderFlow_Cliente.controller;

import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteAtualizacaoParcialRequest;
import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteRequest;
import com.github.joseprandj.OrderFlow_Cliente.dto.ClienteResponse;
import com.github.joseprandj.OrderFlow_Cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@RequestBody @Valid ClienteRequest request) {
        ClienteResponse response = clienteService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cpfCnpj}")
                .buildAndExpand(response.cpfCnpj())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(clienteService.listar(pageable));
    }

    @GetMapping("/{cpfCnpj}")
    public ResponseEntity<ClienteResponse> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        return ResponseEntity.ok(clienteService.buscarPorCpfCnpj(cpfCnpj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable UUID id, @RequestBody @Valid ClienteRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizarParcialmente(@PathVariable UUID id,
                                                                 @RequestBody @Valid ClienteAtualizacaoParcialRequest request) {
        return ResponseEntity.ok(clienteService.atualizarParcialmente(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
