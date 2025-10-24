package br.com.bars_register.domain;

import java.time.LocalDateTime;

public class AuditoriaVenda {
    private int id;
    private int vendaId;
    private LocalDateTime dataHora;
    private double total;
    private String tipoPagamento;

    public AuditoriaVenda() {}

    public AuditoriaVenda(int id, int vendaId, LocalDateTime dataHora, double total, String tipoPagamento) {
        this.id = id;
        this.vendaId = vendaId;
        this.dataHora = dataHora;
        this.total = total;
        this.tipoPagamento = tipoPagamento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVendaId() { return vendaId; }
    public void setVendaId(int vendaId) { this.vendaId = vendaId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }
}