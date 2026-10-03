package com.github.joseprandj.OrderFlow_Estoque.repository;

import com.github.joseprandj.OrderFlow_Estoque.model.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

    Optional<Estoque> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Estoque> findByQuantidadeGreaterThanEqual(int quantidade, Pageable pageable);
}
