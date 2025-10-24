package br.com.bars_register.api;

import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.Venda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public List<Venda> listar() {
        return vendaService.listarVendas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscar(@PathVariable int id) {
        return vendaService.buscarVendaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Venda> registrar(@Valid @RequestBody VendaRequest req) {
        List<ItemVenda> itens = new ArrayList<>();
        for (ItemVendaRequest itemReq : req.itens()) {
            Produto p = new Produto();
            p.setId(itemReq.produtoId());
            ItemVenda item = new ItemVenda(p, itemReq.quantidade());
            itens.add(item);
        }
        Venda criada = vendaService.registrarVenda(itens, req.tipoPagamento());
        return ResponseEntity.created(URI.create("/api/sales/" + criada.getId())).body(criada);
    }

    public record VendaRequest(
            @NotEmpty(message = "Itens da venda são obrigatórios") List<@Valid ItemVendaRequest> itens,
            @NotBlank(message = "Tipo de pagamento é obrigatório") String tipoPagamento
    ) {}

    public record ItemVendaRequest(
            @Min(value = 1, message = "ID de produto inválido") int produtoId,
            @Min(value = 1, message = "Quantidade deve ser ao menos 1") int quantidade
    ) {}
}