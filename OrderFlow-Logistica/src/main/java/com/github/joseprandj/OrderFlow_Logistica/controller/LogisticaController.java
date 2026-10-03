package com.github.joseprandj.OrderFlow_Logistica.controller;

import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaRequest;
import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaResponse;
import com.github.joseprandj.OrderFlow_Logistica.dto.LogisticaStatusRequest;
import com.github.joseprandj.OrderFlow_Logistica.service.LogisticaService;
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
@RequestMapping("/logisticas")
@RequiredArgsConstructor
public class LogisticaController {

    private final LogisticaService logisticaService;

    @PostMapping
    public ResponseEntity<LogisticaResponse> criar(@RequestBody @Valid LogisticaRequest request) {
        LogisticaResponse response = logisticaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public Page<LogisticaResponse> listar(Pageable pageable) {
        return logisticaService.listar(pageable);
    }

    @GetMapping("/{id}")
    public LogisticaResponse buscarPorId(@PathVariable UUID id) {
        return logisticaService.buscarPorId(id);
    }

    @PatchMapping("/{id}/status")
    public LogisticaResponse alterarStatus(@PathVariable UUID id, @RequestBody @Valid LogisticaStatusRequest request) {
        return logisticaService.alterarStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        logisticaService.excluir(id);
    }
}
