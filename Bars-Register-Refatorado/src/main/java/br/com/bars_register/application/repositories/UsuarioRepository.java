package br.com.bars_register.application.repositories;

import java.util.List;
import java.util.Optional;

import br.com.bars_register.domain.Usuario;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Usuario update(Usuario usuario);
    boolean deleteById(int id);
    Optional<Usuario> findById(int id);
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
    List<Usuario> findAll();
}