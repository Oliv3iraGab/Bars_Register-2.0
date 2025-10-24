package br.com.bars_register.infrastructure.jpa;

import jakarta.persistence.*;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;

@Entity
@Table(name = "itens_venda")
public class ItemVendaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;
    
    @Column(nullable = false)
    private Integer quantidade;
    
    @Column(nullable = false)
    private Double subtotal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaEntity venda;
    
    // Construtores
    public ItemVendaEntity() {}
    
    public ItemVendaEntity(ProdutoEntity produto, Integer quantidade, Double subtotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
    }
    
    // Métodos de conversão
    public ItemVenda toDomain() {
        Produto produtoDomain = produto.toDomain();
        return new ItemVenda(produtoDomain, quantidade);
    }
    
    public static ItemVendaEntity fromDomain(br.com.bars_register.domain.ItemVenda item) {
        ItemVendaEntity entity = new ItemVendaEntity();
        entity.quantidade = item.getQuantidade();
        entity.subtotal = item.getSubtotal();
        
        // Criar ProdutoEntity a partir do Produto do domínio
        entity.produto = ProdutoEntity.fromDomain(item.getProduto());
        
        return entity;
    }
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public ProdutoEntity getProduto() { return produto; }
    public void setProduto(ProdutoEntity produto) { this.produto = produto; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public VendaEntity getVenda() { return venda; }
    public void setVenda(VendaEntity venda) { this.venda = venda; }
}
