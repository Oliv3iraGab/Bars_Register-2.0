package br.com.bars_register.api;

import br.com.bars_register.application.services.ProdutoService;
import br.com.bars_register.domain.Produto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProdutoService produtoService;

    public ProductController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> listar() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscar(@PathVariable int id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        Produto criado = produtoService.cadastrar(produto);
        return ResponseEntity.created(URI.create("/api/products/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable int id, @RequestBody Produto produto) {
        return produtoService.buscarPorId(id)
                .map(existente -> {
                    produto.setId(id);
                    Produto atualizado = produtoService.atualizar(produto);
                    return ResponseEntity.ok(atualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        try {
            boolean removido = produtoService.remover(id);
            return removido ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataIntegrityViolationException | org.hibernate.exception.ConstraintViolationException e) {
            // Produto possui vínculos em itens de venda: retornar conflito
            return ResponseEntity.status(409).build();
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Produto> atualizarEstoque(@PathVariable int id, @RequestBody StockUpdateRequest req) {
        produtoService.atualizarEstoque(id, req.delta());
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record StockUpdateRequest(int delta) {}
}