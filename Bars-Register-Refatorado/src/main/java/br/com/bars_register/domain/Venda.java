package br.com.bars_register.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int id;
    private LocalDateTime dataVenda;
    private double total;
    private String tipoPagamento;
    private List<ItemVenda> itens = new ArrayList<>();

    public Venda() {}

    public Venda(int id, LocalDateTime dataVenda, double total, String tipoPagamento) {
        this.id = id;
        this.dataVenda = dataVenda;
        this.total = total;
        this.tipoPagamento = tipoPagamento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    public void adicionarItem(ItemVenda item) {
        this.itens.add(item);
        this.total = calcularTotal();
    }

    public double calcularTotal() {
        return itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();
    }
}