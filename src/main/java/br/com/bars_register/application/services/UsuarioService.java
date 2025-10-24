package br.com.bars_register.application.services;

import java.util.List;
import java.util.Optional;

import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.domain.Usuario;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário é obrigatório");
        }
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Login é obrigatório");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login é obrigatório");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        return usuarioRepository.findByLoginAndSenha(login, senha);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        return usuarioRepository.update(usuario);
    }

    public boolean removerUsuario(int id) {
        return usuarioRepository.deleteById(id);
    }
}