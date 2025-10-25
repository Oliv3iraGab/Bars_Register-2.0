package br.com.bars_register.infrastructure.repo;

import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.domain.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public synchronized Usuario save(Usuario usuario) {
        usuario.setId(seq.getAndIncrement());
        usuarios.add(usuario);
        return usuario;
    }

    @Override
    public synchronized Usuario update(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == usuario.getId()) {
                usuarios.set(i, usuario);
                return usuario;
            }
        }
        throw new IllegalArgumentException("Usuário não encontrado: " + usuario.getId());
    }

    @Override
    public synchronized boolean deleteById(int id) {
        return usuarios.removeIf(u -> u.getId() == id);
    }

    @Override
    public synchronized Optional<Usuario> findById(int id) {
        return usuarios.stream().filter(u -> u.getId() == id).findFirst();
    }

    @Override
    public synchronized Optional<Usuario> findByLoginAndSenha(String login, String senha) {
        return usuarios.stream().filter(u -> login.equals(u.getLogin()) && senha.equals(u.getSenha())).findFirst();
    }

    @Override
    public synchronized List<Usuario> findAll() {
        return new ArrayList<>(usuarios);
    }
}
