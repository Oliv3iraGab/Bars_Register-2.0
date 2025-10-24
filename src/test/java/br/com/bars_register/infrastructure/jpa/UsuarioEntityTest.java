package br.com.bars_register.infrastructure.jpa;

import br.com.bars_register.domain.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioEntityTest {

    @Test
    void toDomain_DeveConverterEntityParaDomain() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity("João Silva", "joao", "joao@email.com", true, "123456");
        entity.setId(1);

        // Act
        Usuario usuario = entity.toDomain();

        // Assert
        assertNotNull(usuario);
        assertEquals(1, usuario.getId());
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao", usuario.getLogin());
        assertEquals("joao@email.com", usuario.getEmail());
        assertTrue(usuario.isStatus());
        assertEquals("joao", usuario.getLogin());
        assertEquals("123456", usuario.getSenha());
    }

    @Test
    void fromDomain_DeveConverterDomainParaEntity() {
        // Arrange
        Usuario usuario = new Usuario(1, "Maria Santos", "maria", "maria@email.com", true, "maria", "654321");

        // Act
        UsuarioEntity entity = UsuarioEntity.fromDomain(usuario);

        // Assert
        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("Maria Santos", entity.getNome());
        assertEquals("maria", entity.getLogin());
        assertEquals("maria@email.com", entity.getEmail());
        assertTrue(entity.getStatus());
        assertEquals("maria", entity.getLogin());
        assertEquals("654321", entity.getSenha());
    }

    @Test
    void gettersAndSetters_DeveFuncionarCorretamente() {
        // Arrange
        UsuarioEntity entity = new UsuarioEntity();

        // Act
        entity.setId(1);
        entity.setNome("Pedro Costa");
        entity.setLogin("pedro");
        entity.setEmail("pedro@email.com");
        entity.setStatus(false);
        entity.setSenha("senha123");

        // Assert
        assertEquals(1, entity.getId());
        assertEquals("Pedro Costa", entity.getNome());
        assertEquals("pedro", entity.getLogin());
        assertEquals("pedro@email.com", entity.getEmail());
        assertFalse(entity.getStatus());
        assertEquals("senha123", entity.getSenha());
    }

    @Test
    void constructor_ComParametros_DeveInicializarCorretamente() {
        // Act
        UsuarioEntity entity = new UsuarioEntity("Ana Lima", "ana", "ana@email.com", true, "senha456");

        // Assert
        assertEquals("Ana Lima", entity.getNome());
        assertEquals("ana", entity.getLogin());
        assertEquals("ana@email.com", entity.getEmail());
        assertTrue(entity.getStatus());
        assertEquals("ana", entity.getLogin());
        assertEquals("senha456", entity.getSenha());
    }

    @Test
    void constructor_SemParametros_DeveCriarInstanciaVazia() {
        // Act
        UsuarioEntity entity = new UsuarioEntity();

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getNome());
        assertNull(entity.getLogin());
        assertNull(entity.getEmail());
        assertNull(entity.getStatus());
        assertNull(entity.getSenha());
    }
}
