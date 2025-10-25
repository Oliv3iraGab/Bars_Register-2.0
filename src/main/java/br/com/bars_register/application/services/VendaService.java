package br.com.bars_register.application.services;

import java.time.LocalDateTime;
import java.util.List;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.Venda;
import br.com.bars_register.infrastructure.jpa.JpaUtil;

public class VendaService {
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    public Venda registrarVenda(List<ItemVenda> itens, String tipoPagamento) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Venda deve possuir ao menos um item");
        }

        return JpaUtil.inTransaction(() -> {
            Venda venda = new Venda();
            venda.setDataVenda(LocalDateTime.now());
            venda.setTipoPagamento(tipoPagamento);

            // Atualiza estoque e compõe itens
            for (ItemVenda item : itens) {
                Produto p = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProduto().getId()));
                if (p.getEstoque() < item.getQuantidade()) {
                    throw new IllegalArgumentException("Estoque insuficiente para o produto: " + p.getNome());
                }
                p.setEstoque(p.getEstoque() - item.getQuantidade());
                produtoRepository.update(p);
                venda.adicionarItem(item);
            }

            venda.setTotal(venda.calcularTotal());
            return vendaRepository.save(venda);
        });
    }
}