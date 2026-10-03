package com.github.joseprandj.OrderFlow_Pedido.repository;

import com.github.joseprandj.OrderFlow_Pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
}
