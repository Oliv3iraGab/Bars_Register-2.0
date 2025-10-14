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
        if (usuario.getLogin() == null || usuario.getSenha() == null) {
            throw new IllegalArgumentException("Login e senha são obrigatórios");
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        return usuarioRepository.findByLoginAndSenha(login, senha);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}