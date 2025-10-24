package br.com.bars_register.infrastructure.jpa;

import jakarta.persistence.*;
import br.com.bars_register.domain.Venda;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class VendaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private LocalDateTime dataVenda;
    
    @Column(nullable = false, length = 50)
    private String tipoPagamento;
    
    @Column(nullable = false)
    private Double total;
    
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemVendaEntity> itens = new ArrayList<>();
    
    // Construtores
    public VendaEntity() {}
    
    public VendaEntity(LocalDateTime dataVenda, String tipoPagamento, Double total) {
        this.dataVenda = dataVenda;
        this.tipoPagamento = tipoPagamento;
        this.total = total;
    }
    
    // Métodos de conversão
    public Venda toDomain() {
        Venda venda = new Venda();
        venda.setId(id);
        venda.setDataVenda(dataVenda);
        venda.setTipoPagamento(tipoPagamento);
        venda.setTotal(total);
        
        // Converter itens
        for (ItemVendaEntity itemEntity : itens) {
            venda.adicionarItem(itemEntity.toDomain());
        }
        
        return venda;
    }
    
    public static VendaEntity fromDomain(Venda venda) {
        VendaEntity entity = new VendaEntity();
        entity.id = venda.getId();
        entity.dataVenda = venda.getDataVenda();
        entity.tipoPagamento = venda.getTipoPagamento();
        entity.total = venda.getTotal();
        
        // Converter itens
        for (var item : venda.getItens()) {
            ItemVendaEntity itemEntity = ItemVendaEntity.fromDomain(item);
            itemEntity.setVenda(entity);
            entity.itens.add(itemEntity);
        }
        
        return entity;
    }
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }
    
    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    
    public List<ItemVendaEntity> getItens() { return itens; }
    public void setItens(List<ItemVendaEntity> itens) { this.itens = itens; }
}
