package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.application.repositories.ProdutoRepository;
import br.com.bars_register.domain.Produto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoRepositoryAdapter implements ProdutoRepository {
    
    private final ProdutoJpaRepository jpaRepository;
    
    public ProdutoRepositoryAdapter(ProdutoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Produto save(Produto produto) {
        ProdutoEntity entity = ProdutoEntity.fromDomain(produto);
        // Se ID é 0, é um novo produto, então não definir ID
        if (produto.getId() == 0) {
            entity.setId(null);
        }
        ProdutoEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Produto update(Produto produto) {
        return save(produto); // JPA save faz update se ID existe
    }
    
    @Override
    public boolean deleteById(int id) {
        return jpaRepository.findById(id)
                .map(entity -> {
                    entity.setAtivo(false);
                    jpaRepository.save(entity);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    public Optional<Produto> findById(int id) {
        return jpaRepository.findById(id)
                .map(ProdutoEntity::toDomain);
    }
    
    @Override
    public List<Produto> findAll() {
        return jpaRepository.findAllByAtivoTrue()
                .stream()
                .map(ProdutoEntity::toDomain)
                .toList();
    }
}
