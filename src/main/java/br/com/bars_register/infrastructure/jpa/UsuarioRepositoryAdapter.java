package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.domain.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {
    
    private final UsuarioJpaRepository jpaRepository;
    
    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.fromDomain(usuario);
        // Se ID é 0, é um novo usuário, então não definir ID
        if (usuario.getId() == 0) {
            entity.setId(null);
        }
        UsuarioEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Usuario update(Usuario usuario) {
        return save(usuario); // JPA save faz update se ID existe
    }
    
    @Override
    public boolean deleteById(int id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public Optional<Usuario> findById(int id) {
        return jpaRepository.findById(id)
                .map(UsuarioEntity::toDomain);
    }
    
    @Override
    public Optional<Usuario> findByLoginAndSenha(String login, String senha) {
        return jpaRepository.findByLoginAndSenhaAndStatus(login, senha, true)
                .map(UsuarioEntity::toDomain);
    }
    
    @Override
    public List<Usuario> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(UsuarioEntity::toDomain)
                .toList();
    }
}
