package br.com.bars_register.application.services;

import java.time.LocalDateTime;
import java.util.List;

import br.com.bars_register.application.repositories.AuditoriaRepository;
import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.domain.AuditoriaVenda;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.Venda;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class VendaService {
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository, AuditoriaRepository auditoriaRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Venda registrarVenda(List<ItemVenda> itens, String tipoPagamento) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Venda deve possuir ao menos um item");
        }
        if (tipoPagamento == null || tipoPagamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de pagamento é obrigatório");
        }

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
            // Garante que o item carregue os dados reais do produto (preço, nome, etc.)
            item.setProduto(p);
            venda.adicionarItem(item);
        }

        venda.setTotal(venda.calcularTotal());
        Venda salva = vendaRepository.save(venda);

        // Auditoria da venda
        AuditoriaVenda auditoria = new AuditoriaVenda();
        auditoria.setVendaId(salva.getId());
        auditoria.setDataHora(salva.getDataVenda());
        auditoria.setTotal(salva.getTotal());
        auditoria.setTipoPagamento(salva.getTipoPagamento());
        auditoriaRepository.save(auditoria);

        return salva;
    }

    public Optional<Venda> buscarVendaPorId(int id) {
        return vendaRepository.findById(id);
    }

    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }
}