package br.com.bars_register.infrastructure.jpa;

import jakarta.persistence.*;
import br.com.bars_register.domain.Produto;

@Entity
@Table(name = "produtos")
public class ProdutoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false)
    private Double preco;
    
    @Column(nullable = false)
    private Integer estoque;

    @Column(nullable = false)
    private Boolean ativo = true;
    
    // Construtores
    public ProdutoEntity() {}
    
    public ProdutoEntity(String nome, Double preco, Integer estoque) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }
    
    // Métodos de conversão
    public Produto toDomain() {
        return new Produto(id, nome, preco, estoque, Boolean.TRUE.equals(ativo));
    }
    
    public static ProdutoEntity fromDomain(Produto produto) {
        ProdutoEntity entity = new ProdutoEntity();
        entity.id = produto.getId();
        entity.nome = produto.getNome();
        entity.preco = produto.getPreco();
        entity.estoque = produto.getEstoque();
        entity.ativo = produto.isAtivo();
        return entity;
    }
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
