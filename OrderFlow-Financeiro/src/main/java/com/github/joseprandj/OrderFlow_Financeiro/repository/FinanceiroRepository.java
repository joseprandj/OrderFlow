package com.github.joseprandj.OrderFlow_Financeiro.repository;

import com.github.joseprandj.OrderFlow_Financeiro.model.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FinanceiroRepository extends JpaRepository<Financeiro, UUID> {

    boolean existsByIdPedido(UUID idPedido);
}
