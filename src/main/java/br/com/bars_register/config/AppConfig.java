package br.com.bars_register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.bars_register.application.repositories.AuditoriaRepository;
import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.application.repositories.VendaRepository;
import br.com.bars_register.application.services.ProdutoService;
import br.com.bars_register.application.services.RelatorioService;
import br.com.bars_register.application.services.UsuarioService;
import br.com.bars_register.application.services.VendaService;
import br.com.bars_register.infrastructure.memory.InMemoryAuditoriaRepository;
import br.com.bars_register.infrastructure.memory.InMemoryProdutoRepository;
import br.com.bars_register.infrastructure.memory.InMemoryUsuarioRepository;
import br.com.bars_register.infrastructure.memory.InMemoryVendaRepository;
import br.com.bars_register.infrastructure.jpa.AuditoriaRepositoryAdapter;
import br.com.bars_register.infrastructure.jpa.ProdutoRepositoryAdapter;
import br.com.bars_register.infrastructure.jpa.UsuarioRepositoryAdapter;
import br.com.bars_register.infrastructure.jpa.VendaRepositoryAdapter;
import br.com.bars_register.infrastructure.jpa.AuditoriaJpaRepository;
import br.com.bars_register.infrastructure.jpa.ProdutoJpaRepository;
import br.com.bars_register.infrastructure.jpa.UsuarioJpaRepository;
import br.com.bars_register.infrastructure.jpa.VendaJpaRepository;

@Configuration
public class AppConfig {

    @Bean
    @Profile("!test")
    public ProdutoRepository produtoRepository(ProdutoJpaRepository jpaRepository) {
        return new ProdutoRepositoryAdapter(jpaRepository);
    }

    @Bean
    @Profile("!test")
    public UsuarioRepository usuarioRepository(UsuarioJpaRepository jpaRepository) {
        return new UsuarioRepositoryAdapter(jpaRepository);
    }

    @Bean
    @Profile("!test")
    public VendaRepository vendaRepository(VendaJpaRepository jpaRepository) {
        return new VendaRepositoryAdapter(jpaRepository);
    }

    @Bean
    @Profile("!test")
    public AuditoriaRepository auditoriaRepository(AuditoriaJpaRepository auditoriaJpaRepository, VendaJpaRepository vendaJpaRepository) {
        return new AuditoriaRepositoryAdapter(auditoriaJpaRepository, vendaJpaRepository);
    }

    @Bean
    @Profile("test")
    public ProdutoRepository produtoRepositoryTest() {
        return new InMemoryProdutoRepository();
    }

    @Bean
    @Profile("test")
    public UsuarioRepository usuarioRepositoryTest() {
        return new InMemoryUsuarioRepository();
    }

    @Bean
    @Profile("test")
    public VendaRepository vendaRepositoryTest() {
        return new InMemoryVendaRepository();
    }

    @Bean
    @Profile("test")
    public AuditoriaRepository auditoriaRepositoryTest() {
        return new InMemoryAuditoriaRepository();
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
    public VendaService vendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository, AuditoriaRepository auditoriaRepository) {
        return new VendaService(vendaRepository, produtoRepository, auditoriaRepository);
    }

    @Bean
    public RelatorioService relatorioService(VendaRepository vendaRepository) {
        return new RelatorioService(vendaRepository);
    }
}