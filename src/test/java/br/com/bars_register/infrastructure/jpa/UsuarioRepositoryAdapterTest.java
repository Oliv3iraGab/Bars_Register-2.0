package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioJpaRepository jpaRepository;

    private UsuarioRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new UsuarioRepositoryAdapter(jpaRepository);
    }

    @Test
    void save_DeveRetornarUsuarioSalvo() {
        // Arrange
        Usuario usuario = new Usuario(0, "João Silva", "joao", "joao@email.com", true, "joao", "123456");
        UsuarioEntity entity = new UsuarioEntity("João Silva", "joao", "joao@email.com", true, "123456");
        entity.setId(1);
        
        when(jpaRepository.save(any(UsuarioEntity.class))).thenReturn(entity);

        // Act
        Usuario resultado = repositoryAdapter.save(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao", resultado.getLogin());
        assertEquals("joao@email.com", resultado.getEmail());
        assertTrue(resultado.isStatus());
        verify(jpaRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void update_DeveRetornarUsuarioAtualizado() {
        // Arrange
        Usuario usuario = new Usuario(1, "Maria Santos", "maria", "maria@email.com", true, "maria", "654321");
        UsuarioEntity entity = new UsuarioEntity("Maria Santos", "maria", "maria@email.com", true, "654321");
        entity.setId(1);
        
        when(jpaRepository.save(any(UsuarioEntity.class))).thenReturn(entity);

        // Act
        Usuario resultado = repositoryAdapter.update(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Maria Santos", resultado.getNome());
        assertEquals("maria", resultado.getLogin());
        assertEquals("maria@email.com", resultado.getEmail());
        assertTrue(resultado.isStatus());
        verify(jpaRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void deleteById_UsuarioExistente_DeveRetornarTrue() {
        // Arrange
        when(jpaRepository.existsById(1)).thenReturn(true);

        // Act
        boolean resultado = repositoryAdapter.deleteById(1);

        // Assert
        assertTrue(resultado);
        verify(jpaRepository).existsById(1);
        verify(jpaRepository).deleteById(1);
    }

    @Test
    void deleteById_UsuarioNaoExistente_DeveRetornarFalse() {
        // Arrange
        when(jpaRepository.existsById(999)).thenReturn(false);

        // Act
        boolean resultado = repositoryAdapter.deleteById(999);

        // Assert
        assertFalse(resultado);
        verify(jpaRepository).existsById(999);
        verify(jpaRepository, never()).deleteById(anyInt());
    }

    @Test
    void findById_UsuarioExistente_DeveRetornarUsuario() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity("João Silva", "joao", "joao@email.com", true, "123456");
        entity.setId(1);
        
        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        // Act
        Optional<Usuario> resultado = repositoryAdapter.findById(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao", resultado.get().getLogin());
        assertEquals("joao@email.com", resultado.get().getEmail());
        assertTrue(resultado.get().isStatus());
        verify(jpaRepository).findById(1);
    }

    @Test
    void findById_UsuarioNaoExistente_DeveRetornarVazio() {
        // Arrange
        when(jpaRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = repositoryAdapter.findById(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository).findById(999);
    }

    @Test
    void findByLoginAndSenha_ComCredenciaisValidas_DeveRetornarUsuario() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity("João Silva", "joao", "joao@email.com", true, "123456");
        entity.setId(1);
        
        when(jpaRepository.findByLoginAndSenhaAndStatus("joao", "123456", true)).thenReturn(Optional.of(entity));

        // Act
        Optional<Usuario> resultado = repositoryAdapter.findByLoginAndSenha("joao", "123456");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao", resultado.get().getLogin());
        verify(jpaRepository).findByLoginAndSenhaAndStatus("joao", "123456", true);
    }

    @Test
    void findByLoginAndSenha_ComCredenciaisInvalidas_DeveRetornarVazio() {
        // Arrange
        when(jpaRepository.findByLoginAndSenhaAndStatus("joao", "senha_errada", true)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = repositoryAdapter.findByLoginAndSenha("joao", "senha_errada");

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository).findByLoginAndSenhaAndStatus("joao", "senha_errada", true);
    }

    @Test
    void findAll_DeveRetornarListaDeUsuarios() {
        // Arrange
        UsuarioEntity entity1 = new UsuarioEntity("João Silva", "joao", "joao@email.com", true, "123456");
        entity1.setId(1);
        
        UsuarioEntity entity2 = new UsuarioEntity("Maria Santos", "maria", "maria@email.com", true, "654321");
        entity2.setId(2);
        
        List<UsuarioEntity> entities = Arrays.asList(entity1, entity2);
        when(jpaRepository.findAll()).thenReturn(entities);

        // Act
        List<Usuario> resultado = repositoryAdapter.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getId());
        assertEquals("João Silva", resultado.get(0).getNome());
        assertEquals(2, resultado.get(1).getId());
        assertEquals("Maria Santos", resultado.get(1).getNome());
        verify(jpaRepository).findAll();
    }
}
