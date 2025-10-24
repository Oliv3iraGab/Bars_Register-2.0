package br.com.bars_register.application.services;

import br.com.bars_register.application.repositories.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    void criarUsuario_ComDadosValidos_DeveRetornarUsuarioSalvo() {
        // Arrange
        Usuario usuario = new Usuario(0, "João Silva", "joao", "joao@email.com", true, "joao", "123456");
        Usuario usuarioSalvo = new Usuario(1, "João Silva", "joao", "joao@email.com", true, "joao", "123456");
        
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        // Act
        Usuario resultado = usuarioService.criarUsuario(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao", resultado.getLogin());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void criarUsuario_ComDadosInvalidos_DeveLancarExcecao() {
        // Arrange
        Usuario usuario = new Usuario(0, "", "", "", true, "", "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.criarUsuario(usuario);
        });
    }

    @Test
    void autenticar_ComCredenciaisValidas_DeveRetornarUsuario() {
        // Arrange
        Usuario usuario = new Usuario(1, "João Silva", "joao", "joao@email.com", true, "joao", "123456");
        when(usuarioRepository.findByLoginAndSenha("joao", "123456")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.autenticar("joao", "123456");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
        verify(usuarioRepository).findByLoginAndSenha("joao", "123456");
    }

    @Test
    void autenticar_ComCredenciaisInvalidas_DeveRetornarVazio() {
        // Arrange
        when(usuarioRepository.findByLoginAndSenha("joao", "senha_errada")).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.autenticar("joao", "senha_errada");

        // Assert
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findByLoginAndSenha("joao", "senha_errada");
    }

    @Test
    void autenticar_ComLoginVazio_DeveLancarExcecao() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticar("", "123456");
        });
    }

    @Test
    void autenticar_ComSenhaVazia_DeveLancarExcecao() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticar("joao", "");
        });
    }

    @Test
    void listarUsuarios_DeveRetornarListaDeUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(
            new Usuario(1, "João Silva", "joao", "joao@email.com", true, "joao", "123456"),
            new Usuario(2, "Maria Santos", "maria", "maria@email.com", true, "maria", "654321")
        );
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.listarUsuarios();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(usuarios, resultado);
        verify(usuarioRepository).findAll();
    }

    @Test
    void buscarPorId_UsuarioExistente_DeveRetornarUsuario() {
        // Arrange
        Usuario usuario = new Usuario(1, "João Silva", "joao", "joao@email.com", true, "joao", "123456");
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
        verify(usuarioRepository).findById(1);
    }

    @Test
    void buscarPorId_UsuarioNaoExistente_DeveRetornarVazio() {
        // Arrange
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(999);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findById(999);
    }

    @Test
    void atualizarUsuario_UsuarioExistente_DeveRetornarUsuarioAtualizado() {
        // Arrange
        Usuario usuario = new Usuario(1, "João Silva Atualizado", "joao", "joao.novo@email.com", true, "joao", "123456");
        when(usuarioRepository.update(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(usuarioRepository).update(usuario);
    }

    @Test
    void removerUsuario_UsuarioExistente_DeveRetornarTrue() {
        // Arrange
        when(usuarioRepository.deleteById(1)).thenReturn(true);

        // Act
        boolean resultado = usuarioService.removerUsuario(1);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void removerUsuario_UsuarioNaoExistente_DeveRetornarFalse() {
        // Arrange
        when(usuarioRepository.deleteById(999)).thenReturn(false);

        // Act
        boolean resultado = usuarioService.removerUsuario(999);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).deleteById(999);
    }
}
