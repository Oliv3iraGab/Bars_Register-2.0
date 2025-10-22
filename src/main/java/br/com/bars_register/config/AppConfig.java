package br.com.bars_register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.application.services.ProdutoService;
import br.com.bars_register.application.services.RelatorioService;
import br.com.bars_register.application.services.UsuarioService;
import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.infrastructure.memory.InMemoryProdutoRepository;
import br.com.bars_register.infrastructure.memory.InMemoryUsuarioRepository;
import br.com.bars_register.infrastructure.memory.InMemoryVendaRepository;

@Configuration
public class AppConfig {

    @Bean
    public ProdutoRepository produtoRepository() {
        return new InMemoryProdutoRepository();
    }

    @Bean
    public UsuarioRepository usuarioRepository() {
        return new InMemoryUsuarioRepository();
    }

    @Bean
    public VendaRepository vendaRepository() {
        return new InMemoryVendaRepository();
    }

    @Bean
    public ProdutoService produtoService(ProdutoRepository produtoRepository) {
        return new ProdutoService(produtoRepository);
    }

    @Bean
    public UsuarioService usuarioService(UsuarioRepository usuarioRepository) {
        return new UsuarioService(usuarioRepository);
    }

    @Bean
    public VendaService vendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        return new VendaService(vendaRepository, produtoRepository);
    }

    @Bean
    public RelatorioService relatorioService(VendaRepository vendaRepository) {
        return new RelatorioService(vendaRepository);
    }
}