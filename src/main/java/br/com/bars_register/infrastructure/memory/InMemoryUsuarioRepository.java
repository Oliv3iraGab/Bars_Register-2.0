package br.com.bars_register.infrastructure.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.bars_register.application.repositories.UsuarioRepository;
import br.com.bars_register.domain.Usuario;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final Map<Integer, Usuario> storage = new HashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override
    public Usuario save(Usuario usuario) {
        int id = seq.getAndIncrement();
        usuario.setId(id);
        storage.put(id, usuario);
        return usuario;
    }

    @Override
    public Usuario update(Usuario usuario) {
        storage.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public boolean deleteById(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public Optional<Usuario> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Usuario> findByLoginAndSenha(String login, String senha) {
        return storage.values().stream()
            .filter(u -> u.getLogin().equals(login) && u.getSenha().equals(senha) && u.isStatus())
            .findFirst();
    }

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(storage.values());
    }
}