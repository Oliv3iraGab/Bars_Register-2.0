package br.com.bars_register.application.repositories;

import java.util.List;
import java.util.Optional;

import br.com.bars_register.domain.Produto;

public interface ProdutoRepository {
    Produto save(Produto produto);
    Produto update(Produto produto);
    boolean deleteById(int id);
    Optional<Produto> findById(int id);
    List<Produto> findAll();
    boolean hasSalesHistory(int id);
}