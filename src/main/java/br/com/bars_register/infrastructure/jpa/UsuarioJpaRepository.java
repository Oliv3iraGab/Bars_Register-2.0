package br.com.bars_register.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByLoginAndSenhaAndStatus(String login, String senha, Boolean status);
}
