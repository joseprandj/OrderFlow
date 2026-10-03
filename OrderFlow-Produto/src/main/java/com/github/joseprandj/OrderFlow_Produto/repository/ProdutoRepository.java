package com.github.joseprandj.OrderFlow_Produto.repository;

import com.github.joseprandj.OrderFlow_Produto.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    boolean existsBySku(String sku);

    Page<Produto> findByAtivo(boolean ativo, Pageable pageable);
}
