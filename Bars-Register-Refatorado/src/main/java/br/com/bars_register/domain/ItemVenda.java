package br.com.bars_register.domain;

public class ItemVenda {
    private Produto produto;
    private int quantidade;

    public ItemVenda() {}

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }
}