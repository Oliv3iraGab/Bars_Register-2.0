package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.domain.AuditoriaVenda;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias_venda")
public class AuditoriaVendaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaEntity venda;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 50)
    private String tipoPagamento;

    public AuditoriaVendaEntity() {}

    public AuditoriaVenda toDomain() {
        AuditoriaVenda a = new AuditoriaVenda();
        a.setId(id);
        a.setVendaId(venda != null ? venda.getId() : 0);
        a.setDataHora(dataHora);
        a.setTotal(total);
        a.setTipoPagamento(tipoPagamento);
        return a;
    }

    public static AuditoriaVendaEntity fromDomain(AuditoriaVenda a, VendaEntity vendaEntity) {
        AuditoriaVendaEntity e = new AuditoriaVendaEntity();
        e.id = a.getId();
        e.venda = vendaEntity;
        e.dataHora = a.getDataHora();
        e.total = a.getTotal();
        e.tipoPagamento = a.getTipoPagamento();
        return e;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public VendaEntity getVenda() { return venda; }
    public void setVenda(VendaEntity venda) { this.venda = venda; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }
}