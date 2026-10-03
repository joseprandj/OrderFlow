package com.github.joseprandj.OrderFlow_Logistica.repository;

import com.github.joseprandj.OrderFlow_Logistica.model.Logistica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogisticaRepository extends JpaRepository<Logistica, UUID> {

    boolean existsByIdPedido(UUID idPedido);
}
