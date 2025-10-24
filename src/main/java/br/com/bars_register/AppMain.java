package br.com.bars_register;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import br.com.bars_register.application.services.ProdutoService;
import br.com.bars_register.application.services.RelatorioService;
import br.com.bars_register.application.services.UsuarioService;
import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.domain.ItemVenda;
import br.com.bars_register.domain.Produto;
import br.com.bars_register.domain.Usuario;
import br.com.bars_register.infrastructure.memory.InMemoryProdutoRepository;
import br.com.bars_register.infrastructure.memory.InMemoryUsuarioRepository;
import br.com.bars_register.infrastructure.memory.InMemoryVendaRepository;
import br.com.bars_register.infrastructure.memory.InMemoryAuditoriaRepository;

public class AppMain {
    public static void main(String[] args) {
        // Setup: repositórios em memória
        var produtoRepo = new InMemoryProdutoRepository();
        var usuarioRepo = new InMemoryUsuarioRepository();
        var vendaRepo = new InMemoryVendaRepository();
        var auditoriaRepo = new InMemoryAuditoriaRepository();

        // Serviços
        var produtoService = new ProdutoService(produtoRepo);
        var usuarioService = new UsuarioService(usuarioRepo);
        var vendaService = new VendaService(vendaRepo, produtoRepo, auditoriaRepo);
        var relatorioService = new RelatorioService(vendaRepo);

        // Teste 1: criar usuário e autenticar
        Usuario admin = new Usuario(0, "Administrador", "ADMIN", "admin@bars.com", true, "admin", "1234");
        usuarioService.criarUsuario(admin);
        var loginOk = usuarioService.autenticar("admin", "1234").isPresent();
        System.out.println("[Teste] Autenticação: " + (loginOk ? "OK" : "FALHOU"));

        // Teste 2: cadastrar produtos e listar
        Produto cerveja = produtoService.cadastrar(new Produto(0, "Cerveja", 10.0, 100));
        Produto refrigerante = produtoService.cadastrar(new Produto(0, "Refrigerante", 7.5, 50));
        List<Produto> produtos = produtoService.listarTodos();
        System.out.println("[Teste] Produtos cadastrados: " + produtos.size());

        // Teste 3: registrar venda e atualizar estoque
        ItemVenda item1 = new ItemVenda(cerveja, 5);
        ItemVenda item2 = new ItemVenda(refrigerante, 3);
        var venda = vendaService.registrarVenda(Arrays.asList(item1, item2), "DINHEIRO");
        System.out.println("[Teste] Venda total: R$ " + venda.getTotal());
        System.out.println("[Teste] Estoque cerveja após venda: " + produtoService.buscarPorId(cerveja.getId()).get().getEstoque());
        System.out.println("[Teste] Estoque refrigerante após venda: " + produtoService.buscarPorId(refrigerante.getId()).get().getEstoque());

        // Teste 4: relatório por dia
        Map<LocalDate, Double> totaisPorDia = relatorioService.totalVendasPorDia(LocalDate.now(), LocalDate.now());
        System.out.println("[Teste] Total do dia: R$ " + totaisPorDia.getOrDefault(LocalDate.now(), 0.0));

        // Teste 5: remoção de produto
        boolean removido = produtoService.remover(refrigerante.getId());
        System.out.println("[Teste] Remoção refrigerante: " + (removido ? "OK" : "FALHOU"));

        // Conclusão
        System.out.println("[Fim] Testes do main concluídos.");
    }
}